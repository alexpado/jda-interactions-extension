package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.impl.DefaultErrorHandler;
import fr.alexpado.jda.interactions.impl.interactions.autocomplete.AutocompleteInteractionContainerImpl;
import fr.alexpado.jda.interactions.impl.interactions.button.ButtonInteractionContainerImpl;
import fr.alexpado.jda.interactions.impl.interactions.slash.SlashInteractionContainerImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionContainer;
import fr.alexpado.jda.interactions.tools.InteractionUtils;
import io.sentry.IScope;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.*;

/**
 * This class is the one containing the main logic to redirect an {@link Interaction} to the right
 * {@link InteractionContainer}.
 *
 * @see InteractionExtension#run(String, Class, Interaction)
 */
@SuppressWarnings("unused")
public class InteractionExtension extends ListenerAdapter {

    private final Map<Class<? extends Interaction>, InteractionEventHandler<?>> handlers;
    private final Map<Class<? extends Interaction>, InteractionContainer<?, ?>> containers;
    private final Collection<InteractionResponseHandler>                        responseHandlers;
    private final Collection<InteractionPreprocessor>                           preprocessors;

    private final SlashInteractionContainer        slashContainer;
    private final ButtonInteractionContainer       buttonContainer;
    private final AutocompleteInteractionContainer autocompleteContainer;
    private       InteractionErrorHandler          errorHandler;

    /**
     * Create a new instance of {@link InteractionExtension}.
     */
    public InteractionExtension() {

        this.handlers         = new HashMap<>();
        this.containers       = new HashMap<>();
        this.responseHandlers = new ArrayList<>();
        this.preprocessors    = new ArrayList<>();
        this.errorHandler     = new DefaultErrorHandler();

        this.slashContainer        = new SlashInteractionContainerImpl();
        this.buttonContainer       = new ButtonInteractionContainerImpl();
        this.autocompleteContainer = new AutocompleteInteractionContainerImpl();

        this.registerContainer(SlashCommandInteraction.class, this.slashContainer);
        this.registerContainer(ButtonInteraction.class, this.buttonContainer);
        this.registerContainer(CommandAutoCompleteInteraction.class, this.autocompleteContainer);
        this.registerHandler(SlashCommandInteraction.class, this.slashContainer);
        this.registerHandler(ButtonInteraction.class, this.buttonContainer);
        this.registerHandler(CommandAutoCompleteInteraction.class, this.autocompleteContainer);
    }

    /**
     * Add all mapping for basic parameter injection.
     */
    public void useDefaultMapping() {

        for (InteractionContainer<?, ?> container : this.containers.values()) {
            container.addClassMapping(User.class, (event, opt) -> event.getInteraction()::getUser);
            container.addClassMapping(Channel.class, (event, opt) -> event.getInteraction()::getChannel);
            container.addClassMapping(GuildChannel.class, (event, opt) -> event.getInteraction()::getGuildChannel);
            container.addClassMapping(Guild.class, (event, opt) -> event.getInteraction()::getGuild);
            container.addClassMapping(ChannelType.class, (event, opt) -> event.getInteraction()::getChannelType);
            container.addClassMapping(InteractionType.class, (event, opt) -> event.getInteraction()::getType);
            container.addClassMapping(Member.class, (event, opt) -> event.getInteraction()::getMember);
            container.addClassMapping(MessageChannel.class, (event, opt) -> event.getInteraction()::getMessageChannel);
            container.addClassMapping(JDA.class, (event, opt) -> event.getInteraction()::getJDA);
            container.addClassMapping(Interaction.class, (event, opt) -> event::getInteraction);
            container.addClassMapping(ITimedAction.class, (event, opt) -> event::getTimedAction);
        }
    }

    /**
     * Retrieve the default {@link SlashInteractionContainer}.
     *
     * @return The default {@link SlashInteractionContainer}.
     */
    public SlashInteractionContainer getSlashContainer() {

        return this.slashContainer;
    }

    /**
     * Retrieve the default {@link ButtonInteractionContainer}.
     *
     * @return The default {@link ButtonInteractionContainer}.
     */
    public ButtonInteractionContainer getButtonContainer() {

        return this.buttonContainer;
    }

    /**
     * Retrieve the default {@link AutocompleteInteractionContainer}.
     *
     * @return The default {@link AutocompleteInteractionContainer}.
     */
    public AutocompleteInteractionContainer getAutocompleteContainer() {

        return this.autocompleteContainer;
    }

    /**
     * Set the {@link InteractionErrorHandler} to use for this instance of {@link InteractionExtension}.
     *
     * @param errorHandler
     *         The {@link InteractionErrorHandler} implementation.
     */
    public void setErrorHandler(InteractionErrorHandler errorHandler) {

        this.errorHandler = errorHandler;
    }

    /**
     * Register a new {@link InteractionEventHandler}.
     *
     * @param forInteraction
     *         The {@link Interaction} class that the provided {@link InteractionEventHandler} can handle.
     * @param handler
     *         The handler to register
     * @param <T>
     *         The type of {@link Interaction}.
     */
    public <T extends Interaction> void registerHandler(Class<T> forInteraction, InteractionEventHandler<T> handler) {

        this.handlers.put(forInteraction, handler);
    }

    /**
     * Register a new {@link InteractionContainer};
     *
     * @param forInteraction
     *         The {@link Interaction} class that the provided {@link InteractionContainer} can handle.
     * @param container
     *         The container to register.
     * @param <T>
     *         The type of the {@link Interaction}.
     */
    public <T extends Interaction> void registerContainer(Class<T> forInteraction, InteractionContainer<?, T> container) {

        this.containers.put(forInteraction, container);
    }

    /**
     * Register a new {@link InteractionResponseHandler}.
     *
     * @param handler
     *         The {@link InteractionResponseHandler} to register.
     */
    public void registerResponseHandler(InteractionResponseHandler handler) {

        this.responseHandlers.add(handler);
    }

    /**
     * Register a new {@link InteractionPreprocessor}.
     *
     * @param preprocessor
     *         The {@link InteractionPreprocessor} to register.
     */
    public void registerPreprocessor(InteractionPreprocessor preprocessor) {

        this.preprocessors.add(preprocessor);
    }

    /**
     * Execute the interaction flow with the provided {@link Interaction}.
     *
     * @param transactionName
     *         The transaction name for this execution.
     * @param type
     *         The class of the interaction.
     * @param discordEvent
     *         The interaction event to handle
     * @param <T>
     *         The type of the interaction
     * @param <V>
     *         The type of the target
     * @param <K>
     *         The type of the container
     */
    // Suppressing warning for unchecked cast as it is type-safe due to the nature of the register methods signature.
    public <T extends Interaction, V extends InteractionTarget<T>, K extends InteractionContainer<V, T>> void run(String transactionName, Class<T> type, T discordEvent) {

        try (ITimedAction timedAction = ITimedAction.create()) {
            timedAction.open(transactionName, "interaction", "Interaction received");

            if (!this.handlers.containsKey(type)) {
                throw new IllegalStateException("No handler for the provided interaction.");
            }

            if (!this.containers.containsKey(type)) {
                throw new IllegalStateException("No container for the provided interaction.");
            }

            timedAction.action("handling", "Handling the interaction");
            InteractionEventHandler<T> handler   = (InteractionEventHandler<T>) this.handlers.get(type);
            DispatchEvent<T>           event     = handler.handle(timedAction, discordEvent);
            K                          container = (K) this.containers.get(type);
            timedAction.endAction();

            try {
                timedAction.action("preprocessors", "Calling preprocessors");

                if (!this.preprocessors.stream().allMatch(preprocessor -> preprocessor.mayContinue(event))) {
                    return; // Ignore this event
                }

                Object result = this.preprocessors.stream()
                                                  .map(preprocessor -> preprocessor.preprocess(event))
                                                  .filter(Optional::isPresent)
                                                  .findFirst()
                                                  .orElse(Optional.empty())
                                                  .orElse(null);

                timedAction.endAction();

                if (result == null) {
                    timedAction.action("dispatching", "Dispatching the interaction");
                    result = container.dispatch(event);
                    timedAction.endAction();
                }

                InteractionResponseHandler responseHandler;

                timedAction.action("answering", "Finding and using the response handler");
                // Prioritize self-contained feature
                if (container instanceof InteractionResponseHandler localHandler && localHandler.canHandle(
                        event,
                        result
                )) {
                    responseHandler = localHandler;
                } else {

                    Object finalResult = result;
                    responseHandler = this.responseHandlers.stream()
                                                           .filter(registeredHandler -> registeredHandler.canHandle(
                                                                   event,
                                                                   finalResult
                                                           ))
                                                           .findAny()
                                                           .orElse(null);
                }

                if (responseHandler == null) {
                    this.errorHandler.onNoResponseHandlerFound(event, result);
                    return;
                }

                responseHandler.handleResponse(event, result);
                timedAction.endAction();
            } catch (Exception e) {
                this.errorHandler.handleException(event, e);
            }
        }
    }

    // <editor-fold desc="Default Listener">
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Sentry.configureScope(scope -> this.createScope(scope, event, "slash", event.getFullCommandName()
                                                                                    .replace(" ", "/")));

        String transaction = "slash://%s".formatted(event.getFullCommandName().replace(" ", "/"));

        this.run(transaction, SlashCommandInteraction.class, event);
        Sentry.popScope();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        Sentry.configureScope(scope -> this.createScope(scope, event, "button", event.getComponentId()));

        String transaction;
        try {
            URI uri = URI.create(event.getComponentId());
            transaction = "%s://%s%s".formatted(uri.getScheme(), uri.getHost(), uri.getPath());
        } catch (Exception ignore) {
            transaction = "button://%s".formatted(event.getComponentId());
        }

        this.run(transaction, ButtonInteraction.class, event);
        Sentry.popScope();
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

        Sentry.configureScope(scope -> this.createScope(scope, event, "auto-complete", event.getFullCommandName()
                                                                                            .replace(" ", "/")));

        String transaction = "complete://%s".formatted(event.getFullCommandName().replace(" ", "/"));
        this.run(transaction, CommandAutoCompleteInteraction.class, event);
        Sentry.popScope();
    }
    // </editor-fold>

    private void createScope(IScope scope, Interaction interaction, String type, String description) {

        Map<String, String> extra   = new HashMap<>();
        User                user    = interaction.getUser();
        Channel             channel = interaction.getChannel();
        Guild               guild   = interaction.getGuild();

        io.sentry.protocol.User sentryUser = new io.sentry.protocol.User();
        sentryUser.setId(user.getId());
        sentryUser.setEmail("%s@discordapp.com".formatted(user.getId()));
        sentryUser.setUsername(user.getName());
        scope.setUser(sentryUser);

        scope.setTag("category", "interaction");
        scope.setTag("type", "auto-complete");
        scope.setTag("interaction", interaction.getId());
        scope.setTag("description", description);

        scope.setTag("user", user.getId());
        extra.put("user", user.getName());

        if (channel != null) {
            scope.setTag("channel", channel.getId());
            extra.put("channel", channel.getName());
        }

        if (guild != null) {
            scope.setTag("guild", guild.getId());
            extra.put("guild", guild.getName());
        }

        if (interaction instanceof SlashCommandInteraction slash) {
            Map<String, Object> options = new HashMap<>();

            for (OptionMapping option : slash.getOptions()) {
                options.put(option.getName(), InteractionUtils.extractOptionValue(option));
            }

            scope.setContexts("Interaction", options);
        }

        scope.setContexts("Discord", extra);
    }

}
