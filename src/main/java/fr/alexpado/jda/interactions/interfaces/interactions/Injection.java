package fr.alexpado.jda.interactions.interfaces.interactions;

import java.util.function.Supplier;

/**
 * Interface used to represent an injection.
 *
 * @param <T>
 *         The type of the event source
 * @param <U>
 *         The type of the mapped value.
 */
public interface Injection<T, U> {

    /**
     * Retrieve the supplier that will allow to lazy-load a {@link U} instance.
     *
     * @param event
     *         The event {@link T} from which information about the context can be used.
     *
     * @return A supplier to lazy-load a value of type {@link U}.
     */
    Supplier<U> inject(T event);

}
