package fr.alexpado.interactions.exceptions;


/**
 * Base exception for all custom runtime exceptions thrown within the interaction handling library.
 */
public class InteractionException extends RuntimeException {

    /**
     * Constructs a new {@link InteractionException} with the specified detail message.
     *
     * @param message
     *         The detail message.
     */
    public InteractionException(String message) {

        super(message);
    }

    public InteractionException(String message, Throwable cause) {

        super(message, cause);
    }

}
