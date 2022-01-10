package fr.alexpado.jda.interactions.interfaces.interactions;

import net.dv8tion.jda.api.EmbedBuilder;

public interface InteractionResponse {

    EmbedBuilder getEmbed();

    int getCode();

    boolean isEphemeral();

}
