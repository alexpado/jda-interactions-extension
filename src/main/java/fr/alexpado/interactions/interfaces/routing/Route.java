package fr.alexpado.interactions.interfaces.routing;

import fr.alexpado.interactions.InteractionRouter;
import org.jetbrains.annotations.NotNull;

import java.net.URI;

/**
 * Represents a registered route within the {@link InteractionRouter}.
 * <p>
 * At its core, a route is defined by a unique URI that maps to a specific handler.
 */
public interface Route {

    /**
     * Retrieves the unique URI for this route.
     *
     * @return The route's URI.
     */
    @NotNull URI getUri();

}
