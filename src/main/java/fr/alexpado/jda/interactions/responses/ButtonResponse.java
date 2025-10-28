package fr.alexpado.jda.interactions.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

import java.util.function.Consumer;

/**
 * Interface representing the default response for a {@link ButtonInteractionTarget}.
 */
public interface ButtonResponse {

    /**
     * Retrieve the {@link MessageRequest} {@link Consumer} that should set the response content.
     *
     * @return A {@link MessageRequest} {@link Consumer}
     */
    Consumer<MessageRequest<?>> getHandler();

    /**
     * Check if instead of sending a new message, the original {@link Message} on which the {@link Button} has been
     * clicked should be edited.
     *
     * @return True if the original {@link Message} should be edited, false otherwise.
     */
    boolean shouldEditOriginalMessage();

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    boolean isEphemeral();

}
