package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.executors.BasicDiscordContainer;
import fr.alexpado.jda.interactions.executors.EmbedPageContainer;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class InteractionManagerImpl implements InteractionManager {

    private final Map<Class<?>, Function<Interaction, ?>> dependencies;
    private final List<InteractionExecutor>               executors;
    private final List<InteractionContainer>              containers;
    private final List<InteractionResponseHandler>        responseHandlers;
    private final InteractionContainer                    defaultContainer;
    private final InteractionErrorHandler                 handler;


    public InteractionManagerImpl(JDA jda, InteractionErrorHandler defaultErrorHandler) {

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

        jda.addEventListener(new InteractionListener(this));

        // Default injection
        this.registerMapping(Interaction.class, interaction -> interaction);
        this.registerMapping(InteractionType.class, Interaction::getType);
        this.registerMapping(Guild.class, Interaction::getGuild);
        this.registerMapping(ChannelType.class, Interaction::getChannelType);
        this.registerMapping(User.class, Interaction::getUser);
        this.registerMapping(Member.class, Interaction::getMember);
        this.registerMapping(Channel.class, Interaction::getChannel);
        this.registerMapping(GuildChannel.class, Interaction::getGuildChannel);
        this.registerMapping(MessageChannel.class, Interaction::getMessageChannel);
        this.registerMapping(JDA.class, Interaction::getJDA);
    }

    private CommandListUpdateAction build(Supplier<CommandListUpdateAction> supplier) {

        CommandListUpdateAction updateAction = supplier.get();

        for (InteractionContainer container : this.containers) {
            updateAction = container.build(updateAction);
        }

        return updateAction;
    }

    /**
     * Get all {@link InteractionItem} present in this {@link InteractionContainer}.
     *
     * @return A list of {@link InteractionItem}.
     */
    @Override
    public List<InteractionItem> getInteractionItems() {

        List<InteractionItem> items = new ArrayList<>();
        for (InteractionContainer container : this.containers) {
            items.addAll(container.getInteractionItems());
        }
        return items;
    }

    /**
     * Register a custom class injection when calling method annotated with {@link Interact}. Parameters will be injected using
     * mapping defined with this method.
     *
     * @param target
     *         The target class for the injection.
     * @param getter
     *         The function that allows converting an {@link Interaction} to the given class.
     */
    @Override
    public <T> void registerMapping(Class<T> target, Function<Interaction, T> getter) {

        if (!this.dependencies.containsKey(target)) {
            this.dependencies.put(target, getter);
        }
    }

    /**
     * Build the underlying {@link InteractionContainer}s globally across all guilds.
     *
     * @param jda
     *         The {@link JDA} instance to use.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    @Override
    public CommandListUpdateAction build(JDA jda) {

        return this.build(jda::updateCommands);
    }

    /**
     * Build the underlying {@link InteractionContainer}s only on the provided guild.
     *
     * @param guild
     *         The {@link Guild} instance to use.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    @Override
    public CommandListUpdateAction build(Guild guild) {

        return this.build(guild::updateCommands);
    }

    /**
     * Dispatch and execute the appropriate actions associated with the provided {@link DispatchEvent}.
     *
     * @param event
     *         A {@link DispatchEvent}.
     */
    @Override
    public void dispatch(DispatchEvent event) {

        // <editor-fold desc="Step 1 - Preprocessing">
        Optional<InteractionExecutor> optionalExecutor = this.executors.stream()
                                                                       .filter(item -> item.canResolve(event.getPath()))
                                                                       .findFirst();

        if (optionalExecutor.isEmpty()) {
            if (event.getInteraction() instanceof ReplyCallbackAction cb) {
                cb.setEmbeds(
                        new EmbedBuilder()
                                .setDescription("Sorry, an error occurred and your request could not be handled.")
                                .setColor(Color.RED)
                                .build()
                ).queue();
            }

            throw new IllegalStateException("No resolver found for path: " + event.getPath());
        }

        InteractionExecutor     executor     = optionalExecutor.get();
        InteractionErrorHandler errorHandler = executor instanceof InteractionErrorHandler handler ? handler : this.handler;
        executor.prepare(event);
        // </editor-fold>

        // <editor-fold desc="Step 2 - Execution">
        Optional<ExecutableItem> optionalExecutable = executor.resolve(event.getPath());

        if (optionalExecutable.isEmpty()) {
            errorHandler.handleNoAction(event);
            return;
        }

        ExecutableItem executable = optionalExecutable.get();

        if (executable instanceof InteractionItem item) {
            if (!item.canExecute(event.getInteraction())) {
                errorHandler.handleNonExecutable(event, item);
                return;
            }

            if (item.getMeta().isDeferred() && event.getInteraction() instanceof IReplyCallback cb) {
                cb.deferReply(item.getMeta().isHidden()).complete();
            }
        }

        InteractionResponse response;
        try {
            response = executable.execute(event, this.dependencies);
        } catch (Exception e) {
            errorHandler.handleException(event, executable, e);
            return;
        }
        // </editor-fold>

        // <editor-fold desc="Step 3 - Response Handling">
        Optional<InteractionResponseHandler> optionalResponseHandler = this.responseHandlers.stream()
                                                                                            .filter(item -> item.canHandle(
                                                                                                    response))
                                                                                            .findFirst();

        if (optionalResponseHandler.isEmpty()) {
            if (event.getInteraction() instanceof IReplyCallback cb) {
                cb.replyEmbeds(InteractionTools.asEmbed(Color.RED, "Unable to display response.")).queue();
                return;
            }

            throw new IllegalStateException("Unable to handle response, and could not notify the user of it either");
        }

        InteractionResponseHandler responseHandler = optionalResponseHandler.get();
        responseHandler.handleResponse(event, executable, response);
        // </editor-fold>

    }

    /**
     * Add a new {@link InteractionExecutor} to this {@link InteractionManager}.
     *
     * @param executor
     *         The {@link InteractionExecutor} instance.
     */
    @Override
    public void addExecutor(InteractionExecutor executor) {

        this.executors.add(executor);
    }

    /**
     * Add a new {@link InteractionResponseHandler} to this {@link InteractionManager}.
     *
     * @param responseHandler
     *         The {@link InteractionResponseHandler} instance.
     */
    @Override
    public void addResponseHandler(InteractionResponseHandler responseHandler) {

        this.responseHandlers.add(responseHandler);
    }

    /**
     * Add the provided object to a list of objects to scan when {@link #build(CommandListUpdateAction)} will be called.
     * <p>
     * The object must have at least one public method annotated with {@link Interact} for this call to serve a purpose.
     *
     * @param holder
     *         The object to add.
     */
    @Override
    public void registerInteraction(Object holder) {

        this.defaultContainer.registerInteraction(holder);
    }

    /**
     * Register a new {@link ExecutableItem} with the provided {@link InteractionMeta}. This allows to register simple interaction
     * on-the-fly without bothering with annotations.
     *
     * @param meta
     *         The {@link InteractionMeta} of the new interaction.
     * @param item
     *         The {@link ExecutableItem} to use when executing the interaction.
     */
    @Override
    public void registerInteraction(InteractionMeta meta, ExecutableItem item) {

        this.defaultContainer.registerInteraction(meta, item);
    }

    /**
     * Register a new {@link InteractionItem}.
     *
     * @param item
     *         The {@link InteractionItem}.
     */
    @Override
    public void registerInteraction(InteractionItem item) {

        this.defaultContainer.registerInteraction(item);
    }

    /**
     * Build this {@link InteractionContainer} and add all {@link InteractionItem} having their interaction type set to
     * {@link fr.alexpado.jda.interactions.enums.InteractionType#SLASH} as Discord Slash Commands.
     *
     * @param updateAction
     *         The {@link CommandListUpdateAction} to use to register slash commands.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    @Override
    public CommandListUpdateAction build(CommandListUpdateAction updateAction) {

        return this.defaultContainer.build(updateAction);
    }

}
