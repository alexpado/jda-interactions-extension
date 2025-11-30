package fr.alexpado.interactions.interfaces.handlers;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for a component that handles a specific type of response returned by a {@link RouteHandler}.
 *
 * @param <T>
 *         The specific type of the response this handler can process (e.g., String, EmbedBuilder).
 */
public interface ResponseHandler<T> {

    /**
     * Handles the response and sends a response back to Discord.
     * <p>
     * This method is responsible for taking the response object and executing the appropriate JDA reply action (e.g.,
     * {@code reply()}, {@code replyEmbeds()}, {@code replyModal()}).
     *
     * @param request
     *         The original interaction request, providing context and the event to reply to.
     * @param response
     *         The response object to be handled.
     */
    void handle(@NotNull Request<? extends IReplyCallback> request, @NotNull T response);

}
