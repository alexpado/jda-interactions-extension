package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;

/**
 * Interface representing an object being capable of converting a Discord event {@link T} to a {@link DispatchEvent}.
 *
 * @param <T>
 *         Type of event that should be converted.
 */
public interface InteractionEventHandler<T extends Interaction> {

    /**
     * Generate the {@link URI} for the provided {@link Interaction}.
     *
     * @param event
     *         The Discord event
     *
     * @return An {@link URI}
     */
    URI getEventUri(T event);

    /**
     * Handle the provided event and wrap it in a {@link DispatchEvent}.
     *
     * @param event
     *         The Discord event
     *
     * @return A {@link DispatchEvent}.
     */
    DispatchEvent<T> handle(T event);

}
