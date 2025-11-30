package fr.alexpado.interactions;

import fr.alexpado.interactions.exceptions.UnsupportedInteractionException;
import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.handlers.ErrorHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.internal.DefaultErrorHandler;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.*;

/**
 * The central orchestrator for the interaction handling framework.
 * <p>
 * This class is responsible for receiving raw JDA {@link Interaction} events and managing the entire lifecycle:
 * <ol>
 *     <li>Passing the event to a suitable {@link SchemeAdapter} to create a {@link Request}.</li>
 *     <li>Sending the {@link Request} to the {@link InteractionRouter}.</li>
 *     <li>Passing the result from the router to the {@link ResponseManager}.</li>
 *     <li>Invoking the {@link ErrorHandler} if any part of the process fails.</li>
 * </ol>
 */
public class InteractionManager {

    private final Map<Class<? extends Interaction>, List<SchemeAdapter<?>>> adapters;

    private final InteractionRouter router;
    private final ResponseManager   responseManager;

    private ErrorHandler errorHandler;


    /**
     * Constructs a new {@link InteractionManager} with default components.
     */
    public InteractionManager() {

        this(new InteractionRouter(), new ResponseManager());
    }

    /**
     * Constructs a new {@link InteractionManager} with provided components.
     * <p>
     * Useful for testing or custom configurations.
     */
    public InteractionManager(InteractionRouter router, ResponseManager responseManager) {

        this.adapters        = new HashMap<>();
        this.router          = router;
        this.responseManager = responseManager;
        this.errorHandler    = new DefaultErrorHandler();
    }

    /**
     * Retrieves the configured {@link InteractionRouter} instance.
     *
     * @return The interaction router.
     */
    public InteractionRouter getRouter() {

        return this.router;
    }

    /**
     * Retrieves the configured {@link ResponseManager} instance.
     *
     * @return The result handler manager.
     */
    public ResponseManager getResponseManager() {

        return this.responseManager;
    }

    /**
     * Sets a custom {@link ErrorHandler} for the interaction lifecycle.
     *
     * @param errorHandler
     *         The error handler to use.
     */
    public void setErrorHandler(ErrorHandler errorHandler) {

        this.errorHandler = Objects.requireNonNull(errorHandler);
    }

    /**
     * Registers a {@link SchemeAdapter} to handle a specific type of JDA {@link Interaction} event.
     *
     * @param eventType
     *         The class of the JDA event (e.g., {@code ButtonInteraction.class}).
     * @param adapter
     *         The adapter implementation that can process this event type.
     * @param <T>
     *         The type of the interaction.
     */
    public <T extends Interaction> void registerAdapter(Class<T> eventType, SchemeAdapter<T> adapter) {

        this.adapters.computeIfAbsent(eventType, k -> new ArrayList<>()).add(adapter);
    }

    /**
     * The main entry point for processing an incoming JDA {@link Interaction} event.
     *
     * @param event
     *         The event to process.
     * @param <T>
     *         The type of the interaction event.
     */
    public <T extends Interaction> void processEvent(T event) {

        Request<T> request = null;

        try {
            List<SchemeAdapter<?>> potentialAdapters = this.adapters.get(event.getClass());

            if (potentialAdapters == null) {
                throw new UnsupportedInteractionException(
                        "No adapter found for event type %s".formatted(event.getClass().getName()),
                        event
                );
            }

            for (SchemeAdapter<?> genericAdapter : potentialAdapters) {
                SchemeAdapter<T> adapter = (SchemeAdapter<T>) genericAdapter;

                Optional<Request<T>> optRequest = adapter.createRequest(event);

                if (optRequest.isPresent()) {
                    request = optRequest.get();
                    Object result = this.getRouter().dispatch(request);
                    this.getResponseManager().processResult(request, result);
                    return;
                }
            }
        } catch (Throwable e) {
            this.errorHandler.handle(e, event, request);
        }
    }

}
