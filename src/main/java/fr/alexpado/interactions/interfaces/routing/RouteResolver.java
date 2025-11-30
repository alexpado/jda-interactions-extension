package fr.alexpado.interactions.interfaces.routing;

import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.Optional;

/**
 * Interface representing a class capable of resolving a {@link Request} into an {@link Endpoint}.
 */
public interface RouteResolver {

    /**
     * Attempts to retrieve an {@link Endpoint} capable of handling the provided {@link Request}.
     *
     * @param request
     *         The {@link Request} from which the {@link Endpoint} should be resolved.
     * @param <T>
     *         The type of the interaction in the request.
     *
     * @return An optional {@link Endpoint} if the {@link Request} can be handled by this {@link RouteResolver}
     */
    <T extends Interaction> Optional<Endpoint<?>> resolve(Request<T> request);

}
