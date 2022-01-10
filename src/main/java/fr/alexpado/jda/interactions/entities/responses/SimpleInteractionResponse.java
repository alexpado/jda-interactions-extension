package fr.alexpado.jda.interactions.entities.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
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


    @Override
    public EmbedBuilder getEmbed() {

        return this.builder;
    }

    @Override
    public int getCode() {

        return this.code;
    }

    @Override
    public boolean isEphemeral() {

        return this.ephemeral;
    }

}
