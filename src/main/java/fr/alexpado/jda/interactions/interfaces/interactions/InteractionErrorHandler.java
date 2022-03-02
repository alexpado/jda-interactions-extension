package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import net.dv8tion.jda.api.interactions.Interaction;

public interface InteractionErrorHandler {

    /**
     * Called when an exception occurs during the execution of an {@link ExecutableItem}.
     *
     * @param event
     *         The {@link DispatchEvent} used when the error occurred.
     * @param item
     *         The {@link ExecutableItem} generating the error.
     * @param exception
     *         The {@link Exception} thrown.
     */
    void handleException(DispatchEvent event, ExecutableItem item, Exception exception);

    /**
     * Called when {@link DispatchEvent#getPath()} did not match any {@link InteractionItem}.
     *
     * @param event
     *         The unmatched {@link DispatchEvent}.
     */
    void handleNoAction(DispatchEvent event);

    /**
     * Called when an {@link InteractionItem} has been matched but could not be executed due to its filter ({@link
     * InteractionItem#canExecute(JdaInteraction)}.
     *
     * @param event
     *         The {@link DispatchEvent} used.
     * @param item
     *         The {@link InteractionItem} that could not be executed.
     */
    void handleNonExecutable(DispatchEvent event, InteractionItem item);

}
