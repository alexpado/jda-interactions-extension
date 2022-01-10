package fr.alexpado.jda.interactions.entities.responses;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.EmbedBuilder;

public class SimpleInteractionResponse implements InteractionResponse {

    private final EmbedBuilder builder;
    private final int          code;
    private final boolean      ephemeral;

    public SimpleInteractionResponse(EmbedBuilder builder, int code) {

        this.builder   = builder;
        this.code      = code;
        this.ephemeral = false;
    }

    public SimpleInteractionResponse(EmbedBuilder builder, boolean ephemeral) {

        this.builder   = builder;
        this.code      = 0;
        this.ephemeral = ephemeral;
    }


    /**
     * Retrieves the {@link EmbedBuilder} representing the result of this {@link InteractionResponse}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder getEmbed() {

        return this.builder;
    }

    /**
     * Retrieves this {@link InteractionResponse}'s code. This isn't used in this library, but you can still use it when
     * you have a command depending on another.
     *
     * @return A code.
     */
    @Override
    public int getCode() {

        return this.code;
    }

    /**
     * Define if this {@link InteractionResponse} should be displayed only to the user running the current interaction.
     * This value won't be applied everytime due do {@link Interact} and {@link InteractionMeta} policy.
     *
     * @return True if hidden, false otherwise.
     */
    @Override
    public boolean isEphemeral() {

        return this.ephemeral;

    }

}
