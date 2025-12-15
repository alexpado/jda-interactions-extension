package fr.alexpado.jda.interactions.entities;

import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class wrapping an {@link Interaction} with additional data to help any {@link InteractionContainer} to match a
 * {@link InteractionTarget} to execute.
 *
 * @param <T>
 *         The type of the {@link Interaction}.
 */
@Deprecated
public record DispatchEvent<T extends Interaction>(
        ITimedAction timedAction,
        URI path,
        T interaction,
        Map<String, Object> options
) {

    /**
     * Create a new {@link DispatchEvent} with the provided path and {@link Interaction}.
     *
     * @param timedAction
     *         An {@link ITimedAction} implementation allowing to time performance.
     * @param path
     *         The {@link URI} representing the path of the {@link InteractionTarget} to execute.
     * @param interaction
     *         The {@link Interaction} that caused this {@link DispatchEvent} creation.
     */
    public DispatchEvent(ITimedAction timedAction, URI path, T interaction) {

        this(timedAction, path, interaction, new HashMap<>());
    }

    /**
     * Create a new {@link DispatchEvent} with the provided path and {@link Interaction}.
     *
     * @param timedAction
     *         An {@link ITimedAction} implementation allowing to time performance.
     * @param path
     *         The {@link URI} representing the path of the {@link InteractionTarget} to execute.
     * @param interaction
     *         The {@link Interaction} that caused this {@link DispatchEvent} creation.
     * @param options
     *         The additional options to use when executing the {@link InteractionTarget}.
     */
    public DispatchEvent {

    }

    /**
     * Retrieve an {@link ITimedAction} implementation allowing to time performance.
     *
     * @return An {@link ITimedAction}
     */
    @Override
    public ITimedAction timedAction() {

        return this.timedAction;
    }

    /**
     * Retrieve the {@link URI} representing the path of the {@link InteractionTarget} to execute.
     *
     * @return An {@link URI}.
     */
    @Override
    public URI path() {

        return this.path;
    }

    /**
     * The {@link Interaction} that caused this {@link DispatchEvent} to be created.
     *
     * @return An {@link Interaction}.
     */
    @Override
    public T interaction() {

        return this.interaction;
    }

    /**
     * Retrieve the additional data that can be used to execute an {@link InteractionTarget}.
     *
     * @return A possibly empty {@link Map}
     */
    @Override
    public Map<String, Object> options() {

        return this.options;
    }

}
