package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interface representing an object being able to preprocess a {@link DispatchEvent}.
 */
public interface InteractionPreprocessor {

    /**
     * Preprocess a {@link DispatchEvent} before it get dispatched.
     *
     * @param event
     *         The {@link DispatchEvent} ready to be dispatched.
     * @param <T>
     *         The {@link Interaction} type
     *
     * @return An optional object. If an object is returned, it will be used the same way as an
     *         {@link InteractionTarget} response and will be used when displaying the result to the user, without the
     *         {@link InteractionTarget} being called. In other word, returning an object will completely cancel the
     *         execution of the {@link InteractionTarget}.
     */
    <T extends Interaction> Optional<Object> preprocess(@NotNull DispatchEvent<T> event);

}
