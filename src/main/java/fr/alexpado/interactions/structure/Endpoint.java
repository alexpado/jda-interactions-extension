package fr.alexpado.interactions.structure;

import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Route;
import net.dv8tion.jda.api.interactions.Interaction;

/**
 * Record representing a {@link Route} and its corresponding {@link RouteHandler}.
 *
 * @param route
 *         The {@link Route}
 * @param handler
 *         The {@link Route}'s {@link RouteHandler}
 * @param interactionType
 *         The {@link Interaction} this {@link Endpoint} can manage.
 * @param <T>
 *         The type of the interaction in the request.
 */
public record Endpoint<T extends Interaction>(Route route, RouteHandler<T> handler, Class<T> interactionType) {

}
