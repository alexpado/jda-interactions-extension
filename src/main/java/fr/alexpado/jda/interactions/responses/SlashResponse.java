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
}
