package fr.alexpado.jda.interactions.interfaces;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Interface representing an object that can be represented as a {@link MessageEmbed}.
 */
@Deprecated
public interface DiscordEmbeddable {

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    EmbedBuilder asEmbed();

    /**
     * In case this {@link DiscordEmbeddable} is an {@link Exception}, check if the message should be displayed to
     * everyone. If {@code false}, the message will be ephemeral.
     *
     * @return True if public, false otherwise.
     */
    boolean showToEveryone();

}
