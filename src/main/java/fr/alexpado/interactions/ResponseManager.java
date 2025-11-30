package fr.alexpado.interactions;

import fr.alexpado.interactions.exceptions.ResultHandlerNotFoundException;
import fr.alexpado.interactions.interfaces.handlers.ResponseHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the registration and execution of {@link ResponseHandler}s.
 * <p>
 * This class takes the raw object returned from a route handler and dispatches it to the appropriate
 * {@link ResponseHandler} based on its type.
 */
public class ResponseManager {

    /**
     * We use a LinkedHashMap to preserve the order of registration. This is crucial for polymorphic lookups, ensuring
     * that a handler for a more specific type (e.g., ArrayList) is checked before a more general one (e.g., List,
     * Collection).
     */
    private final Map<Class<?>, ResponseHandler<?>> handlers = new LinkedHashMap<>();

    /**
     * Registers a new {@link ResponseHandler} for a specific result type.
     *
     * @param resultType
     *         The class of the result that this handler can process.
     * @param handler
     *         The handler implementation.
     * @param <T>
     *         The type of the result.
     */
    public <T> void registerHandler(Class<T> resultType, ResponseHandler<T> handler) {

        if (resultType.equals(Void.class)) {
            throw new IllegalArgumentException("Cannot register a response handler for Void type.");
        }

        this.handlers.put(resultType, handler);
    }

    /**
     * Processes a result object by finding and executing a compatible {@link ResponseHandler}.
     *
     * @param request
     *         The interaction request.
     * @param result
     *         The result object to process.
     */
    public void processResult(Request<?> request, Object result) {

        if (result == null) {
            throw new NullPointerException("Cannot handle null result");
        }

        if (!(request.getEvent() instanceof IReplyCallback)) {
            return;
        }

        Request<? extends IReplyCallback> replyableRequest = (Request<? extends IReplyCallback>) request;

        for (Map.Entry<Class<?>, ResponseHandler<?>> entry : this.handlers.entrySet()) {
            if (entry.getKey().isInstance(result)) {
                ResponseHandler<Object> handler = (ResponseHandler<Object>) entry.getValue();

                handler.handle(replyableRequest, result);
                return;
            }
        }

        throw new ResultHandlerNotFoundException(
                "No ResultHandler found for type: " + result.getClass().getName(),
                request,
                result
        );
    }

}
