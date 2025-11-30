package fr.alexpado.interactions.exceptions;

import fr.alexpado.interactions.ResponseManager;
import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Request;


/**
 * Exception thrown when the {@link ResponseManager} cannot find a suitable handler for the object returned by a
 * {@link RouteHandler}.
 */
public class ResultHandlerNotFoundException extends InteractionException {

    private final Request<?> request;
    private final Object     result;

    /**
     * Creates a new instance of {@link ResultHandlerNotFoundException}.
     *
     * @param message
     *         A descriptive message.
     * @param request
     *         The request that was being processed.
     * @param result
     *         The result object for which no handler could be found.
     */
    public ResultHandlerNotFoundException(String message, Request<?> request, Object result) {

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
