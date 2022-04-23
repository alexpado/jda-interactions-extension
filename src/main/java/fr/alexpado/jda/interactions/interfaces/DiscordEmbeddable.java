package fr.alexpado.jda.interactions.interfaces;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Interface representing an object that can be represented as a {@link MessageEmbed}.
 */
public interface DiscordEmbeddable {

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    EmbedBuilder asEmbed();

}
