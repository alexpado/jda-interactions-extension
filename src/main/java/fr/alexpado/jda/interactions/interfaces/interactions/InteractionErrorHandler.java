package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;

/**
 * Interface representing an error handler for all {@link InteractionContainer}.
 */
public interface InteractionErrorHandler {

    /**
     * Called when an exception occurs during the execution of an {@link Interaction}.
     *
     * @param event
     *         The {@link DispatchEvent} that was being executed when the exception was thrown.
     * @param exception
     *         The thrown {@link Exception}.
     * @param <T>
     *         The type of the interaction.
     */
    <T extends Interaction> void handleException(DispatchEvent<T> event, Exception exception);

    /**
     * Called when no {@link InteractionResponseHandler} could be found for the provided object.
     *
     * @param event
     *         The {@link DispatchEvent} that was used to generate the response.
     * @param response
     *         The response object generated.
     * @param <T>
     *         The type of the interaction.
     */
    <T extends Interaction> void onNoResponseHandlerFound(DispatchEvent<T> event, Object response);

}
