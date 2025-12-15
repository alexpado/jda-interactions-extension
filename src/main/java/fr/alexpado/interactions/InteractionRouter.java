package fr.alexpado.interactions;

import fr.alexpado.interactions.exceptions.InteractionException;
import fr.alexpado.interactions.exceptions.UnknownInteractionException;
import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Interceptor;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.Route;
import fr.alexpado.interactions.interfaces.routing.RouteResolver;
import fr.alexpado.interactions.internal.DeferrableInterceptor;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;
import java.util.*;


/**
 * Responsible for mapping an incoming {@link Request} to a specific {@link RouteHandler}.
 * <p>
 * This class holds the routing table and manages the execution of {@link Interceptor}s before and after the handler is
 * invoked.
 */
public class InteractionRouter implements RouteResolver {

    private final Map<URI, Endpoint<?>> endpoints;
    private final Set<Interceptor>      interceptors;
    private final Set<RouteResolver>    resolvers;

    /**
     * Constructs a new, empty {@link InteractionRouter}.
     */
    public InteractionRouter() {

        this.endpoints    = new HashMap<>();
        this.interceptors = new HashSet<>();
        this.resolvers    = new HashSet<>();

        // Ensure the developers don't have to support deferring themselves.
        this.registerInterceptor(new DeferrableInterceptor());
    }

    /**
     * Registers a new route and its handler with the router.
     *
     * @param route
     *         The route definition, containing the URI.
     * @param interactionType
     *         The specific class of {@link Interaction} that the handler expects.
     * @param handler
     *         The handler that will execute the logic for this route.
     * @param <T>
     *         The type of the interaction.
     *
     * @return {@code true} if the route was registered successfully, {@code false} if a route with the same normalized
     *         URI already exists.
     */
    public <T extends Interaction> boolean registerRoute(Route route, Class<T> interactionType, RouteHandler<T> handler) {

        URI uri = Request.normalize(route.getUri());

        if (this.endpoints.containsKey(uri)) {
            return false;
        }

        this.endpoints.put(uri, new Endpoint<>(route, handler, interactionType));
        return true;
    }

    /**
     * Registers a new {@link Interceptor} to be applied to all requests.
     *
     * @param interceptor
     *         The interceptor to add.
     *
     * @return {@code true} if the interceptor was added, {@code false} if it was already present.
     */
    public boolean registerInterceptor(Interceptor interceptor) {

        return this.interceptors.add(interceptor);
    }

    /**
     * Registers a new {@link RouteResolver} to act as a fallback router. Resolvers are queried in the order they are
     * registered.
     *
     * @param resolver
     *         The resolver to add to the dispatch chain.
     *
     * @return {@code true} if the resolver was added, {@code false} if it was already present.
     */
    public boolean registerResolver(RouteResolver resolver) {

        return this.resolvers.add(resolver);
    }

    @Override
    public <T extends Interaction> Optional<Endpoint<?>> resolve(Request<T> request) {

        URI         uri         = Request.normalize(request.getUri());
        Endpoint<?> rawEndpoint = this.endpoints.get(uri);

        if (rawEndpoint != null) {
            return java.util.Optional.of(rawEndpoint);
        }

        for (RouteResolver resolver : this.resolvers) {
            Optional<Endpoint<?>> maybeEndpoint = resolver.resolve(request);
            if (maybeEndpoint.isPresent()) {
                return maybeEndpoint;
            }
        }

        return Optional.empty();
    }


    /**
     * Dispatches a {@link Request} to the appropriate {@link RouteHandler}.
     * <p>
     * This method performs the route lookup, runs interceptors, invokes the handler, and returns the result.
     *
     * @param request
     *         The incoming request to dispatch.
     * @param <T>
     *         The type of the interaction in the request.
     *
     * @return The object returned by the handler or an interceptor.
     *
     * @throws UnknownInteractionException
     *         if no route matches the request's URI.
     * @throws InteractionException
     *         if a route is found but its handler is not compatible with the request's interaction type.
     */
    public <T extends Interaction> Object dispatch(Request<T> request) {

        Optional<Endpoint<T>> optionalEndpoint = this.localResolve(request);

        if (optionalEndpoint.isEmpty()) {
            throw new UnknownInteractionException("No target found to handle the interaction request.", request);
        }

        Endpoint<T> endpoint = optionalEndpoint.get();

        Route           route   = endpoint.route();
        RouteHandler<T> handler = endpoint.handler();

        Optional<Object> preHandleResult = this.preHandle(endpoint, request);

        if (preHandleResult.isPresent()) {
            return preHandleResult.get();
        }

        try {
            Object result = handler.handle(request);
            return this.postHandle(endpoint, request, result).orElse(result);
        } catch (RuntimeException e) {
            this.postHandle(endpoint, request, e);
            throw e;
        }
    }

    private <T extends Interaction> Optional<Endpoint<T>> localResolve(Request<T> request) {

        return this.resolve(request).map(endpoint -> this.asSafeEndpoint(request, endpoint));
    }

    private <T extends Interaction> Endpoint<T> asSafeEndpoint(Request<T> request, Endpoint<?> endpoint) {

        if (!endpoint.interactionType().isAssignableFrom(request.getEvent().getClass())) {
            throw new InteractionException(
                    String.format(
                            "Route found for URI '%s', but its handler for %s cannot process an event of type %s.",
                            request.getUri(),
                            endpoint.interactionType().getSimpleName(),
                            request.getEvent().getClass().getSimpleName()
                    )
            );
        }

        // This cast is now safe to do.
        return (Endpoint<T>) endpoint;
    }

    private <T extends Interaction> Optional<Object> preHandle(Endpoint<T> endpoint, Request<T> request) {

        for (Interceptor interceptor : this.interceptors) {
            Optional<Object> opt = interceptor.preHandle(endpoint, request);
            if (opt.isPresent()) {
                return opt;
            }
        }

        return Optional.empty();
    }

    private <T extends Interaction> Optional<Object> postHandle(Endpoint<T> endpoint, Request<T> request, Object result) {

        for (Interceptor interceptor : this.interceptors) {
            Optional<Object> opt = interceptor.postHandle(endpoint, request, result);
            if (opt.isPresent()) {
                return opt;
            }
        }

        return Optional.empty();
    }

}
