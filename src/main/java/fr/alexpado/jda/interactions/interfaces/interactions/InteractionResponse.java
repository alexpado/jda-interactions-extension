package fr.alexpado.jda.interactions.interfaces.interactions;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;
import java.util.Optional;

/**
 * Interface representing an {@link InteractionTarget} result.
 */
public interface InteractionResponse {

    /**
     * Retrieve the optional content of this {@link InteractionResponse}.
     *
     * @return The optional content
     */
    Optional<String> getContent();

    /**
     * Retrieve the list of all embeds present in this {@link InteractionResponse}.
     *
     * @return A list of embeds
     */
    List<MessageEmbed> getEmbeds();

}
