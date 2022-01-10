package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.executors.BasicDiscordContainer;
import fr.alexpado.jda.interactions.executors.EmbedPageContainer;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class InteractionManagerImpl implements InteractionManager {

    private final Map<Class<?>, Function<Interaction, ?>> dependencies;
    private final List<InteractionExecutor>               executors;
    private final List<InteractionContainer>              containers;
    private final List<InteractionResponseHandler>        responseHandlers;
    private final InteractionContainer                    defaultContainer;
    private final InteractionErrorHandler                 handler;


    public InteractionManagerImpl(JDABuilder builder, InteractionErrorHandler defaultErrorHandler) {

        this.dependencies     = new HashMap<>();
        this.executors        = new ArrayList<>();
        this.containers       = new ArrayList<>();
        this.responseHandlers = new ArrayList<>();
        this.handler          = defaultErrorHandler;

        BasicDiscordContainer basicDiscordContainer = new BasicDiscordContainer();
        EmbedPageContainer    embedPageContainer    = new EmbedPageContainer();

        this.addExecutor(basicDiscordContainer);
        this.addExecutor(embedPageContainer);
        this.addResponseHandler(basicDiscordContainer);
        this.addResponseHandler(embedPageContainer);

        this.containers.add(basicDiscordContainer);
        this.defaultContainer = basicDiscordContainer;

        builder.addEventListeners(new InteractionListener(this));
    }

    private CommandListUpdateAction build(Supplier<CommandListUpdateAction> supplier) {

        CommandListUpdateAction updateAction = supplier.get();

        for (InteractionContainer container : this.containers) {
            updateAction = container.build(updateAction);
        }

        return updateAction;
    }

    @Override
    public List<InteractionItem> getInteractionItems() {

        List<InteractionItem> items = new ArrayList<>();
        for (InteractionContainer container : this.containers) {
            items.addAll(container.getInteractionItems());
        }
        return items;
    }

    @Override
    public final <T> void registerMapping(Class<T> target, Function<Interaction, T> getter) {

        if (!this.dependencies.containsKey(target)) {
            this.dependencies.put(target, getter);
        }
    }

    @Override
    public final CommandListUpdateAction build(JDA jda) {

        return this.build(jda::updateCommands);
    }

    @Override
    public final CommandListUpdateAction build(Guild guild) {

        return this.build(guild::updateCommands);
    }

    @Override
    public final void dispatch(DispatchEvent event) {

        System.out.println("Received call to: " + event.getPath());

        // Find a resolver
        Optional<InteractionExecutor> optionalExecutor = this.executors.stream()
                .filter(item -> item.canResolve(event.getPath()))
                .findFirst();

        if (optionalExecutor.isEmpty()) {
            event.getInteraction().replyEmbeds(
                    new EmbedBuilder()
                            .setDescription("Sorry, an error occurred and your request could not be handled.")
                            .setColor(Color.RED)
                            .build()
            ).queue();
            throw new IllegalStateException("No resolver found for path: " + event.getPath());
        }

        InteractionExecutor     executor     = optionalExecutor.get();
        InteractionErrorHandler errorHandler = executor instanceof InteractionErrorHandler handler ? handler : this.handler;
        executor.prepare(event);


        Optional<ExecutableItem> optionalExecutable = executor.resolve(event.getPath());

        if (optionalExecutable.isEmpty()) {
            errorHandler.handleNoAction(event);
            return;
        }

        ExecutableItem executable = optionalExecutable.get();

        if (executable instanceof InteractionItem item) {
            if (!item.canExecute(event.getInteraction())) {
                errorHandler.handleNonExecutable(event, item);
            }
        }

        InteractionResponse response;
        try {
            response = executable.execute(event, this.dependencies);
        } catch (Exception e) {
            errorHandler.handleException(event, executable, e);
            return;
        }

        // Rerun executor searching
        Optional<InteractionResponseHandler> optionalResponseHandler = this.responseHandlers.stream()
                .filter(item -> item.canHandle(response))
                .findFirst();

        if (optionalResponseHandler.isEmpty()) {
            event.getInteraction().replyEmbeds(InteractionTools.asEmbed(Color.RED, "Unable to display response."))
                    .queue();
            return;
        }

        InteractionResponseHandler responseHandler = optionalResponseHandler.get();
        responseHandler.handleResponse(event, response);
    }

    @Override
    public void addExecutor(InteractionExecutor resolver) {

        this.executors.add(resolver);
    }

    @Override
    public void addResponseHandler(InteractionResponseHandler responseHandler) {

        this.responseHandlers.add(responseHandler);
    }

    @Override
    public <T> void registerInteraction(T holder) {

        this.defaultContainer.registerInteraction(holder);
    }

    @Override
    public void registerInteraction(InteractionMeta meta, ExecutableItem item) {

        this.defaultContainer.registerInteraction(meta, item);
    }

    @Override
    public void registerInteraction(InteractionItem item) {

        this.defaultContainer.registerInteraction(item);
    }

    @Override
    public CommandListUpdateAction build(CommandListUpdateAction updateAction) {

        return this.defaultContainer.build(updateAction);
    }

}
