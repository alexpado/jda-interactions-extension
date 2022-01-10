package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;

public interface InteractionResponseHandler {

    boolean canHandle(InteractionResponse response);

    void handleResponse(DispatchEvent event, InteractionResponse response);

}
