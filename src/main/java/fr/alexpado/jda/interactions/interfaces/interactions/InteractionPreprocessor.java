package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.InteractionExtension;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interface representing an object being able to preprocess a {@link DispatchEvent}.
 */
public interface InteractionPreprocessor {

    /**
     * Check if the provided {@link DispatchEvent} should be handled. This may prove useful if you want to completely
     * ignore some interaction (not acknowledging them).
     *
     * @param event
     *         The {@link DispatchEvent} ready to be dispatched.
     * @param <T>
     *         The {@link Interaction} type
     *
     * @return True if this {@link DispatchEvent} should be handled, false otherwise.
     */
    <T extends Interaction> boolean mayContinue(@NotNull DispatchEvent<T> event);

    /**
     * Preprocess a {@link DispatchEvent} before it get dispatched. If an object is returned, it will be used the same
     * way as an {@link InteractionTarget} response and will be used when displaying the result to the user, without the
     * {@link InteractionTarget} being called. In other word, returning an object will completely cancel the execution
     * of the {@link InteractionTarget}.
     * <p>
     * Returning an object will not stop other {@link InteractionPreprocessor} being called by
     * {@link InteractionExtension}.
     *
     * @param event
     *         The {@link DispatchEvent} ready to be dispatched.
     * @param <T>
     *         The {@link Interaction} type
     *
     * @return An optional object.
     */
    <T extends Interaction> Optional<Object> preprocess(@NotNull DispatchEvent<T> event);

}
