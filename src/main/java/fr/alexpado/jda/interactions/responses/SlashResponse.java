package fr.alexpado.jda.interactions.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import net.dv8tion.jda.api.entities.Message;

/**
 * Interface representing the default response for a {@link SlashInteractionTarget}.
 */
public interface SlashResponse {

    /**
     * Retrieve the {@link Message} that should be sent as response.
     *
     * @return A {@link Message}
     */
    Message getMessage();

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    boolean isEphemeral();
}
