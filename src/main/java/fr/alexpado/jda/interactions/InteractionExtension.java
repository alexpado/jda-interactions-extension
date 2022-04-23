package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.impl.DefaultErrorHandler;
import fr.alexpado.jda.interactions.impl.interactions.autocomplete.AutocompleteInteractionContainerImpl;
import fr.alexpado.jda.interactions.impl.interactions.button.ButtonInteractionContainerImpl;
import fr.alexpado.jda.interactions.impl.interactions.slash.SlashInteractionContainerImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionContainer;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the one containing the main logic to redirect an {@link Interaction} to the right
 * {@link InteractionContainer}.
 *
 * @see InteractionExtension#run(Class, Interaction)
 */
@SuppressWarnings("unused")
public class InteractionExtension extends ListenerAdapter {

    private final Map<Class<? extends Interaction>, InteractionEventHandler<?>> handlers;
    private final Map<Class<? extends Interaction>, InteractionContainer<?, ?>> containers;
    private final Collection<InteractionResponseHandler>                        responseHandlers;

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
     * Execute the interaction flow with the provided {@link Interaction}.
     *
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
    @SuppressWarnings("unchecked")
    public <T extends Interaction, V extends InteractionTarget<T>, K extends InteractionContainer<V, T>> void run(Class<T> type, T discordEvent) {

        if (!this.handlers.containsKey(type)) {
            throw new IllegalStateException("No handler for the provided interaction.");
        }

        if (!this.containers.containsKey(type)) {
            throw new IllegalStateException("No container for the provided interaction.");
        }

        InteractionEventHandler<T> handler   = (InteractionEventHandler<T>) this.handlers.get(type);
        DispatchEvent<T>           event     = handler.handle(discordEvent);
        K                          container = (K) this.containers.get(type);

        try {
            Object                     result = container.dispatch(event);
            InteractionResponseHandler responseHandler;

            // Prioritize self-contained feature
            if (container instanceof InteractionResponseHandler localHandler && localHandler.canHandle(event, result)) {
                responseHandler = localHandler;
            } else {

                responseHandler = this.responseHandlers.stream()
                                                       .filter(registeredHandler -> registeredHandler.canHandle(event, result))
                                                       .findAny()
                                                       .orElse(null);
            }

            if (responseHandler == null) {
                this.errorHandler.onNoResponseHandlerFound(event, result);
                return;
            }

            responseHandler.handleResponse(event, result);
        } catch (Exception e) {
            this.errorHandler.handleException(event, e);
        }
    }

    // <editor-fold desc="Default Listener">
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        this.run(SlashCommandInteraction.class, event);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        this.run(ButtonInteraction.class, event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {

        this.run(CommandAutoCompleteInteraction.class, event);
    }
    // </editor-fold>
}
