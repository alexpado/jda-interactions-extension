package fr.alexpado.interactions.interfaces.handlers;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the endpoint for a specific route, containing the business logic to execute for an interaction.
 *
 * @param <T>
 *         The specific type of {@link Interaction} this handler can process.
 */
public interface RouteHandler<T extends Interaction> {

    /**
     * Executes the business logic for this route.
     *
     * @param request
     *         The incoming interaction request, guaranteed to be of type {@code T}.
     *
     * @return An object representing the result of the execution, which will be processed by a {@link ResponseHandler}.
     */
    @NotNull Object handle(@NotNull Request<T> request);

}
