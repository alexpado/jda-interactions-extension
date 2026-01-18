package fr.alexpado.interactions.exceptions;

import fr.alexpado.interactions.interfaces.routing.Request;

public class UnsupportedResultException extends InteractionException {

    private final Request<?> request;
    private final Object     result;

    /**
     * Creates a new instance of {@link UnsupportedResultException}.
     *
     * @param message
     *         A descriptive message.
     * @param request
     *         The request that was being processed.
     * @param result
     *         The result object for which no handler could be found.
     */
    public UnsupportedResultException(String message, Request<?> request, Object result) {

        super(message);
        this.request = request;
        this.result  = result;
    }

    /**
     * @return The request that was being processed.
     */
    public Request<?> getRequest() {

        return this.request;
    }

    /**
     * @return The result object for which no handler was found.
     */
    public Object getResult() {

        return this.result;
    }

}
