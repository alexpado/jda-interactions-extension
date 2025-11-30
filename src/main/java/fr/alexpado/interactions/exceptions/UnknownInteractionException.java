package fr.alexpado.interactions.exceptions;

import fr.alexpado.interactions.InteractionRouter;
import fr.alexpado.interactions.interfaces.routing.Request;


/**
 * Exception thrown by the {@link InteractionRouter} when it receives a {@link Request} for which no corresponding route
 * has been registered.
 */
public class UnknownInteractionException extends InteractionException {

    private final Request<?> request;

    /**
     * Constructs a new {@link UnknownInteractionException}.
     *
     * @param message
     *         The detail message.
     * @param request
     *         The request for which no route was found.
     */
    public UnknownInteractionException(String message, Request<?> request) {

        super(message);
        this.request = request;
    }

    /**
     * Retrieves the request that could not be routed.
     *
     * @return The unhandled {@link Request}.
     */
    public Request<?> getRequest() {

        return this.request;
    }

}
