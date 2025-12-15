package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.Nullable;

/**
 * Interface representing an object being able to handle a specific type of interaction response defined by
 * {@link #canHandle(DispatchEvent, Object)}.
 */
@Deprecated
public interface InteractionResponseHandler {

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided response.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The object representing the response given by an interaction.
     * @param <T>
     *         Type of the interaction.
     *
     * @return True if able to handle, false otherwise.
     */
    <T extends Interaction> boolean canHandle(DispatchEvent<T> event, @Nullable Object response);

    /**
     * Handle the response resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The {@link Object} to handle.
     * @param <T>
     *         Type of the interaction.
     */
    <T extends Interaction> void handleResponse(DispatchEvent<T> event, @Nullable Object response);

}
