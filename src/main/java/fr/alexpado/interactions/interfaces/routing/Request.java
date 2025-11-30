package fr.alexpado.interactions.interfaces.routing;

import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Represents a standardized, abstracted view of an incoming JDA {@link Interaction}.
 * <p>
 * This interface decouples the routing and handling logic from the specifics of any particular JDA event, treating all
 * interactions as routable requests.
 *
 * @param <T>
 *         The underlying JDA {@link Interaction} event type.
 */
public interface Request<T extends Interaction> {

    /**
     * Normalizes a {@link URI} by removing its query and fragment components.
     * <p>
     * This is used to ensure that a URI with parameters (e.g., from a button) can match a route that was registered
     * with a base URI.
     *
     * @param uri
     *         The URI to normalize.
     *
     * @return A new URI containing only the scheme, authority, and path.
     *
     * @throws IllegalArgumentException
     *         if the input URI is malformed.
     */
    static @NotNull URI normalize(@NotNull URI uri) {

        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI provided for normalization", e);
        }
    }

    /**
     * Retrieves the original JDA {@link Interaction} event that initiated this request.
     *
     * @return The JDA event.
     */
    @NotNull T getEvent();

    /**
     * Retrieves the unique URI that identifies this interaction.
     *
     * @return The interaction's URI.
     */
    @NotNull URI getUri();

    /**
     * Retrieves the main data payload of the interaction.
     * <p>
     * For a slash command, this would be the command options. For a modal, this would be the submitted input values.
     *
     * @return An immutable map representing the request parameters.
     */
    @NotNull Map<String, Object> getParameters();

    /**
     * Retrieves a mutable map of attributes for this request.
     * <p>
     * This map can be used by {@link Interceptor}s to pass information to the {@link RouteHandler} (e.g., an
     * authenticated user object).
     *
     * @return A map of request attributes.
     */
    @NotNull Map<String, Object> getAttributes();

    /**
     * Add a class attachment to this request. Attachment are special kind of unnamed attributes allowing only a maximum
     * of one instance per type.
     *
     * @param attachmentClass
     *         Class of the attachment
     * @param attachment
     *         Instance of the attachment
     * @param <A>
     *         Type of the attachment
     */
    <A> void addAttachment(@NotNull Class<A> attachmentClass, @NotNull A attachment);

    /**
     * Retrieve an attachment by its class. If the attachment does not exist in this request, the returned value will be
     * {@code null}.
     *
     * @param attachmentClass
     *         Class of the attachment
     * @param <A>
     *         Type of the attachment
     *
     * @return The attachment matching the provided class, or {@code null}.
     */
    <A> @Nullable A getAttachment(Class<A> attachmentClass);

}
