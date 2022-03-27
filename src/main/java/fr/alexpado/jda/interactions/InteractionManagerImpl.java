package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.executors.BasicDiscordContainer;
import fr.alexpado.jda.interactions.executors.EmbedPageContainer;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class InteractionManagerImpl implements InteractionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionManagerImpl.class);

    private final Map<Class<?>, Function<JdaInteraction, ?>> dependencies;
    private final List<InteractionExecutor>                  executors;
    private final List<InteractionContainer>                 containers;
    private final List<InteractionResponseHandler>           responseHandlers;
    private final InteractionContainer                       defaultContainer;
    private final InteractionErrorHandler                    handler;


    public InteractionManagerImpl(JDA jda, InteractionErrorHandler defaultErrorHandler) {

        LOGGER.debug("Initializing interaction manager...");
        this.dependencies     = new HashMap<>();
        this.executors        = new ArrayList<>();
        this.containers       = new ArrayList<>();
        this.responseHandlers = new ArrayList<>();
        this.handler          = defaultErrorHandler;

        LOGGER.debug("Registering containers...");
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
        LOGGER.debug("Registering injections...");
        this.registerMapping(InteractionType.class, JdaInteraction::getType);
        this.registerMapping(Guild.class, JdaInteraction::getGuild);
        this.registerMapping(ChannelType.class, JdaInteraction::getChannelType);
        this.registerMapping(User.class, JdaInteraction::getUser);
        this.registerMapping(Member.class, JdaInteraction::getMember);
        this.registerMapping(Channel.class, JdaInteraction::getChannel);
        this.registerMapping(InteractionHook.class, JdaInteraction::getHook);
        this.registerMapping(GuildChannel.class, JdaInteraction::getGuildChannel);
        this.registerMapping(MessageChannel.class, JdaInteraction::getMessageChannel);
        this.registerMapping(TextChannel.class, JdaInteraction::getTextChannel);
        this.registerMapping(NewsChannel.class, JdaInteraction::getNewsChannel);
        this.registerMapping(VoiceChannel.class, JdaInteraction::getVoiceChannel);
        this.registerMapping(PrivateChannel.class, JdaInteraction::getPrivateChannel);
        this.registerMapping(JDA.class, JdaInteraction::getJDA);
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
     * Register a custom class injection when calling method annotated with {@link Interact}. Parameters will be
     * injected using mapping defined with this method.
     *
     * @param target
     *         The target class for the injection.
     * @param getter
     *         The function that allows converting an {@link Interaction} to the given class.
     */
    @Override
    public <T> void registerMapping(Class<T> target, Function<JdaInteraction, T> getter) {

        if (!this.dependencies.containsKey(target)) {
            LOGGER.debug("Mapping {}", target.getSimpleName());
            this.dependencies.put(target, getter);
        }
    }

    /**
     * Build the underlying {@link InteractionContainer}s globally across all guilds.
     *
     * @param jda
     *         The {@link JDA} instance to use.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call {@link
     *         CommandListUpdateAction#queue()}.
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
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call {@link
     *         CommandListUpdateAction#queue()}.
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
        LOGGER.info("[{}] Dispatching...", event.getPath());
        Optional<InteractionExecutor> optionalExecutor = this.executors.stream()
                                                                       .filter(item -> item.canResolve(event.getPath()))
                                                                       .findFirst();

        if (optionalExecutor.isEmpty()) {

            LOGGER.warn("[{}] No executor found.", event.getPath());
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
        LOGGER.debug("[{}] Preparing event...", event.getPath());
        executor.prepare(event);
        LOGGER.debug("[{}] Ok.", event.getPath());
        // </editor-fold>

        // <editor-fold desc="Step 2 - Execution">
        LOGGER.info("[{}] Resolving executable...", event.getPath());
        Optional<ExecutableItem> optionalExecutable = executor.resolve(event.getPath());

        if (optionalExecutable.isEmpty()) {
            LOGGER.warn("[{}] No executable found.", event.getPath());
            errorHandler.handleNoAction(event);
            return;
        }

        ExecutableItem executable = optionalExecutable.get();

        if (executable instanceof InteractionItem item) {
            LOGGER.debug("[{}] InteractionItem encountered. Processing additional checks...", event.getPath());
            if (!item.canExecute(event.getInteraction())) {
                LOGGER.warn("[{}] InteractionItem isn't executable in the current context.", event.getPath());
                errorHandler.handleNonExecutable(event, item);
                return;
            }

            if (item.getMeta().isDeferred()) {
                LOGGER.info("[{}] Deferring reply...", event.getPath());
                event.getInteraction().deferReply(item.getMeta().isHidden()).complete();
            }
        }

        InteractionResponse response;
        try {
            LOGGER.info("[{}] Executing...", event.getPath());
            response = executable.execute(event, this.dependencies);
            LOGGER.info("[{}] Execution finished.", event.getPath());
        } catch (Exception e) {
            LOGGER.warn("[{}] An error occurred while executing.", event.getPath());
            errorHandler.handleException(event, executable, e);
            return;
        }
        // </editor-fold>

        // <editor-fold desc="Step 3 - Response Handling">
        LOGGER.info("[{}] Handling response...", event.getPath());
        Optional<InteractionResponseHandler> optionalResponseHandler = this.responseHandlers.stream()
                                                                                            .filter(item -> item.canHandle(response))
                                                                                            .findFirst();

        if (optionalResponseHandler.isEmpty()) {
            LOGGER.warn("[{}] No response handler found.", event.getPath());
            event.getInteraction()
                 .replyEmbeds(InteractionTools.asEmbed(Color.RED, "Unable to display response."))
                 .queue();
            return;
        }

        InteractionResponseHandler responseHandler = optionalResponseHandler.get();
        LOGGER.info("[{}] Handling response...", event.getPath());
        responseHandler.handleResponse(event, executable, response);
        LOGGER.info("[{}] Interaction execution finished.", event.getPath());
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

        LOGGER.debug("New executor: {}", executor.getClass().getSimpleName());
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

        LOGGER.debug("New response handler: {}", responseHandler.getClass().getSimpleName());
        this.responseHandlers.add(responseHandler);
    }

    /**
     * Add the provided object to a list of objects to scan when {@link #build(CommandListUpdateAction)} will be
     * called.
     * <p>
     * The object must have at least one public method annotated with {@link Interact} for this call to serve a
     * purpose.
     *
     * @param holder
     *         The object to add.
     */
    @Override
    public void registerInteraction(Object holder) {

        LOGGER.debug("New interaction object: {}", holder.getClass().getSimpleName());
        this.defaultContainer.registerInteraction(holder);
    }

    /**
     * Register a new {@link ExecutableItem} with the provided {@link InteractionMeta}. This allows to register simple
     * interaction on-the-fly without bothering with annotations.
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

        LOGGER.debug("New interaction: {}", item.getPath());
        this.defaultContainer.registerInteraction(item);
    }

    /**
     * Build this {@link InteractionContainer} and add all {@link InteractionItem} having their interaction type set to
     * {@link fr.alexpado.jda.interactions.enums.InteractionType#SLASH} as Discord Slash Commands.
     *
     * @param updateAction
     *         The {@link CommandListUpdateAction} to use to register slash commands.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call {@link
     *         CommandListUpdateAction#queue()}.
     */
    @Override
    public CommandListUpdateAction build(CommandListUpdateAction updateAction) {

        return this.defaultContainer.build(updateAction);
    }

}
