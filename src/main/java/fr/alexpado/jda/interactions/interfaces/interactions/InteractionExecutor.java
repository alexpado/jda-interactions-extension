package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;

import java.net.URI;
import java.util.Optional;

public interface InteractionExecutor {

    /**
     * Check if this {@link InteractionExecutor} can be used to retrieve an {@link ExecutableItem} with the given URI.
     *
     * @param uri
     *         The {@link ExecutableItem} URI.
     *
     * @return True if this {@link InteractionExecutor} can handle the request.
     */
    boolean canResolve(URI uri);

    /**
     * Try to match an {@link ExecutableItem} with the provided URI.
     *
     * @param path
     *         The {@link ExecutableItem} URI.
     *
     * @return An optional {@link ExecutableItem}.
     */
    Optional<ExecutableItem> resolve(URI path);

    /**
     * Called when the {@link DispatchEvent} is ready and is about to be used on an {@link ExecutableItem}. Here you can add
     * custom options.
     *
     * @param event
     *         The {@link DispatchEvent} that will be used.
     */
    void prepare(DispatchEvent event);

}
