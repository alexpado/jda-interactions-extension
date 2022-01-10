package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.EmbedBuilder;

public interface InteractionResponse {

    /**
     * Retrieves the {@link EmbedBuilder} representing the result of this {@link InteractionResponse}.
     *
     * @return An {@link EmbedBuilder}.
     */
    EmbedBuilder getEmbed();

    /**
     * Retrieves this {@link InteractionResponse}'s code. This isn't used in this library, but you can still use it when
     * you have a command depending on another.
     *
     * @return A code.
     */
    int getCode();

    /**
     * Define if this {@link InteractionResponse} should be displayed only to the user running the current interaction.
     * This value won't be applied everytime due do {@link Interact} and {@link InteractionMeta} policy.
     *
     * @return True if hidden, false otherwise.
     */
    boolean isEphemeral();

}
