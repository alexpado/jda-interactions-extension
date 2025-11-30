package fr.alexpado.interactions.internal;

import fr.alexpado.interactions.interfaces.routing.DeferrableRoute;
import fr.alexpado.interactions.interfaces.routing.Interceptor;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.Route;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * An internal {@link Interceptor} that handles the acknowledgment of interactions for routes implementing
 * {@link DeferrableRoute}.
 * <p>
 * If a route is deferred, the resulting {@link InteractionHook} is attached to the {@link Request} for downstream
 * usage.
 */
public class DeferrableInterceptor implements Interceptor {

    @Override
    public Optional<Object> preHandle(@NotNull Route route, @NotNull Request<?> request) {

        if (route instanceof DeferrableRoute deferrableRoute && request.getEvent() instanceof IReplyCallback callback) {
            if (deferrableRoute.isDeferred()) {
                InteractionHook hook = callback.deferReply(deferrableRoute.isEphemeral()).complete();
                request.addAttachment(InteractionHook.class, hook);
            }
        }

        return Optional.empty();
    }

}
