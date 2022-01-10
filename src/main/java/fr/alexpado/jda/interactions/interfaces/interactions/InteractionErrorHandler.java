package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;

public interface InteractionErrorHandler {

    void handleException(DispatchEvent event, ExecutableItem item, Exception exception);

    void handleNoAction(DispatchEvent event);

    void handleNonExecutable(DispatchEvent event, InteractionItem item);

}
