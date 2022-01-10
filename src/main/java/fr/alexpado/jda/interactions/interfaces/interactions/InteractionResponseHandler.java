package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;

public interface InteractionResponseHandler {

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided {@link InteractionResponse}.
     *
     * @param response
     *         The generated {@link InteractionResponse}.
     *
     * @return True if able to handle, false otherwise.
     */
    boolean canHandle(InteractionResponse response);

    /**
     * Handle the {@link InteractionResponse} resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the {@link InteractionResponse}.
     * @param executable
     *         The {@link ExecutableItem} that has been used to generate the {@link InteractionResponse}.
     * @param response
     *         The {@link InteractionResponse} to handle.
     */
    void handleResponse(DispatchEvent event, ExecutableItem executable, InteractionResponse response);

}
