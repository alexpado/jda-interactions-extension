package fr.alexpado.interactions.interfaces.routing;

import fr.alexpado.interactions.annotations.Deferrable;
import fr.alexpado.interactions.internal.DeferrableInterceptor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.URI;

/**
 * A specialized {@link Route} that provides configuration for automatic interaction deferral.
 * <p>
 * Routes implementing this interface will be processed by the {@link DeferrableInterceptor} to automatically
 * acknowledge the interaction before the handler is invoked.
 */
public interface DeferrableRoute extends Route {

    /**
     * Checks if this route should trigger a deferred reply ("Thinking..." state).
     *
     * @return {@code true} to defer the interaction, {@code false} otherwise.
     */
    default boolean isDeferred() {

        return false;
    }

    /**
     * Checks if the deferred reply should be ephemeral.
     *
     * @return {@code true} for an ephemeral reply, {@code false} for a public one.
     */
    default boolean isEphemeral() {

        return false;
    }

    /**
     * Create a {@link DeferrableRoute} based on the provided {@link Method}.
     *
     * @param uri
     *         The route's {@link URI}.
     * @param method
     *         The target {@link Method} for the route.
     *
     * @return A {@link DeferrableRoute}.
     */
    static DeferrableRoute of(URI uri, Method method) {

        boolean defer     = false;
        boolean ephemeral = false;

        if (method.isAnnotationPresent(Deferrable.class)) {
            Deferrable d = method.getAnnotation(Deferrable.class);
            defer     = true;
            ephemeral = d.ephemeral();
        }

        boolean finalDefer     = defer;
        boolean finalEphemeral = ephemeral;

        return new DeferrableRoute() {
            @Override
            public boolean isDeferred() {return finalDefer;}

            @Override
            public boolean isEphemeral() {return finalEphemeral;}

            @Override
            public @NotNull URI getUri() {return uri;}
        };
    }

}
