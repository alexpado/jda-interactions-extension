package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface representing an object being capable of holding a reference to multiple {@link InteractionTarget}.
 *
 * @param <T>
 *         The {@link InteractionTarget} precise type.
 * @param <V>
 *         The type of event the {@link InteractionTarget} is capable of handling.
 */
@Deprecated
public interface InteractionContainer<T extends InteractionTarget<V>, V extends Interaction> {

    /**
     * Retrieve the schema for the {@link URI} of each {@link InteractionTarget}.
     *
     * @return The schema
     */
    String getInteractionSchema();

    /**
     * Create an {@link URI} matching this {@link InteractionContainer}.
     *
     * @param path
     *         The path without schema.
     *
     * @return An {@link URI}.
     */
    URI createURI(String path);

    /**
     * Register the provided {@link InteractionTarget} into this {@link InteractionContainer}.
     *
     * @param target
     *         The {@link InteractionTarget} to register.
     *
     * @return True if it has been registered, false otherwise.
     */
    boolean register(T target);

    /**
     * Retrieve all {@link InteractionTarget} registered so far.
     *
     * @return A list of {@link InteractionTarget}
     */
    Map<URI, T> getInteractions();

    /**
     * Try to find an {@link InteractionTarget} matching the {@link URI}.
     *
     * @param uri
     *         The {@link URI} to match.
     *
     * @return An optional {@link InteractionTarget}
     */
    Optional<T> resolve(URI uri);

    /**
     * Called when an {@link DispatchEvent} is being fired.
     *
     * @param event
     *         The {@link DispatchEvent}
     *
     * @return An object representing the interaction result.
     *
     * @throws Exception
     *         Any exception is possible, as exception thrown by an {@link InteractionTarget} will be forwarded.
     */
    Object dispatch(DispatchEvent<V> event) throws Exception;

    /**
     * Add a mapping class to the dependency map used as parameters injection source. Using {@link Supplier} allow to
     * lazy-load the value.
     *
     * @param clazz
     *         The class type to map.
     * @param mapper
     *         The {@link Injection} allowing a lazy-load of the mapping value.
     * @param <K>
     *         The class type to map.
     */
    <K> void addClassMapping(Class<K> clazz, Injection<DispatchEvent<V>, K> mapper);

    /**
     * Retrieve all mapping used for the parameter injection.
     *
     * @return A map.
     */
    Map<Class<?>, Injection<DispatchEvent<V>, ?>> getMappedClasses();
}
