package fr.alexpado.jda.interactions.entities;

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
public class DispatchEvent<T extends Interaction> {

    private final URI                 path;
    private final T                   interaction;
    private final Map<String, Object> options;

    /**
     * Create a new {@link DispatchEvent} with the provided path and {@link Interaction}.
     *
     * @param path
     *         The {@link URI} representing the path of the {@link InteractionTarget} to execute.
     * @param interaction
     *         The {@link Interaction} that caused this {@link DispatchEvent} creation.
     */
    public DispatchEvent(URI path, T interaction) {

        this(path, interaction, new HashMap<>());
    }

    /**
     * Create a new {@link DispatchEvent} with the provided path and {@link Interaction}.
     *
     * @param path
     *         The {@link URI} representing the path of the {@link InteractionTarget} to execute.
     * @param interaction
     *         The {@link Interaction} that caused this {@link DispatchEvent} creation.
     * @param options
     *         The additional options to use when executing the {@link InteractionTarget}.
     */
    public DispatchEvent(URI path, T interaction, Map<String, Object> options) {

        this.path        = path;
        this.interaction = interaction;
        this.options     = options;
    }

    /**
     * Retrieve the {@link URI} representing the path of the {@link InteractionTarget} to execute.
     *
     * @return An {@link URI}.
     */
    public URI getPath() {

        return this.path;
    }

    /**
     * The {@link Interaction} that caused this {@link DispatchEvent} to be created.
     *
     * @return An {@link Interaction}.
     */
    public T getInteraction() {

        return this.interaction;
    }

    /**
     * Retrieve the additional data that can be used to execute an {@link InteractionTarget}.
     *
     * @return A possibly empty {@link Map}
     */
    public Map<String, Object> getOptions() {

        return this.options;
    }
}
