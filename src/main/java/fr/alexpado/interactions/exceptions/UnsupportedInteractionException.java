package fr.alexpado.interactions.exceptions;

import fr.alexpado.interactions.InteractionManager;
import fr.alexpado.interactions.interfaces.SchemeAdapter;
import net.dv8tion.jda.api.interactions.Interaction;

/**
 * Exception thrown by the {@link InteractionManager} when it receives a JDA {@link Interaction} event for which no
 * suitable {@link SchemeAdapter} has been registered.
 */
public class UnsupportedInteractionException extends InteractionException {

    private final Interaction interaction;

    /**
     * Constructs a new {@link UnsupportedInteractionException}.
     *
     * @param message
     *         The detail message.
     * @param interaction
     *         The JDA {@link Interaction} event that could not be processed.
     */
    public UnsupportedInteractionException(String message, Interaction interaction) {

        super(message);
        this.interaction = interaction;
    }

    /**
     * Retrieves the JDA event that could not be handled.
     *
     * @return The unhandled {@link Interaction}.
     */
    public Interaction getInteraction() {

        return this.interaction;
    }

}
