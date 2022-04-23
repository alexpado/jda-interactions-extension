package fr.alexpado.jda.interactions.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * Interface representing the default response for a {@link ButtonInteractionTarget}.
 */
public interface ButtonResponse {

    /**
     * Retrieve the {@link Message} that should be sent as response.
     *
     * @return A {@link Message}
     */
    Message getMessage();

    /**
     * Check if instead of sending a new message, the original {@link Message} on which the {@link Button} has been
     * clicked should be edited.
     *
     * @return True if the original {@link Message} should be edited, false otherwise.
     */
    boolean shouldEditOriginalMessage();

}
