package fr.alexpado.interactions.interfaces.routing;

import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Defines methods for intercepting interaction requests before and after they are handled by a {@link RouteHandler}.
 * <p>
 * Interceptors are useful for cross-cutting concerns such as logging, permission checks, or modifying requests and
 * responses.
 */
public interface Interceptor {

    /**
     * Intercepts the request before it is passed to the {@link RouteHandler}.
     *
     * @param route
     *         The route that will handle the request.
     * @param request
     *         The incoming interaction request.
     *
     * @return An {@link Optional} containing a result object to short-circuit the execution. If a value is present, the
     *         handler will not be called, and the value will be passed directly to the result handling stage. If the
     *         optional is empty, execution proceeds normally.
     */
    default Optional<Object> preHandle(@NotNull Route route, @NotNull Request<?> request) {

        return Optional.empty();
    }

    /**
     * Intercepts the request after the {@link RouteHandler} has finished execution, but before the result is handled.
     *
     * @param route
     *         The route that handled the request.
     * @param request
     *         The interaction request.
     * @param result
     *         The result object returned by the handler.
     *
     * @return An {@link Optional} containing a new result object to replace the one returned by the handler. If the
     *         optional is empty, the original result is used.
     */
    default Optional<Object> postHandle(@NotNull Route route, @NotNull Request<?> request, @NotNull Object result) {

        return Optional.empty();
    }

}
