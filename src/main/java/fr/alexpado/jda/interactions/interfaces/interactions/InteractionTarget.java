package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.Map;

/**
 * Interface representing an action that can be performed during an {@link Interaction}.
 *
 * @param <T>
 *         Type of the event that will trigger this {@link InteractionTarget}.
 */
public interface InteractionTarget<T extends Interaction> {

    /**
     * Run this {@link InteractionTarget}.
     *
     * @param event
     *         The event responsible for this execution.
     * @param mapping
     *         Map of dependencies used for the parameter injection.
     *
     * @return An {@link Object} representing the result of the execution. The result can be used directly without any
     *         result handler.
     *
     * @throws Exception
     *         If the execution could not occur, or due to an userland exception defined in the interaction.
     * @see InteractionResponseHandler
     */
    Object execute(DispatchEvent<T> event, Map<Class<?>, Injection<DispatchEvent<T>, ?>> mapping) throws Exception;

}
