package fr.alexpado.jda.interactions.impl.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.exceptions.InteractionNotFoundException;
import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Class implementing the default behaviour of an {@link InteractionContainer}.
 *
 * @param <T>
 *         The type of the {@link InteractionTarget} being contained.
 * @param <U>
 *         The type of the {@link Interaction}.
 */
public abstract class DefaultInteractionContainer<T extends InteractionTarget<U> & MetaContainer, U extends Interaction> implements InteractionContainer<T, U>, InteractionEventHandler<U> {

    private final Map<URI, T>                    interactions;
    private final Map<Class<?>, Injection<U, ?>> mappings;

    /**
     * Create a new instance of this {@link InteractionContainer} implementation.
     */
    public DefaultInteractionContainer() {

        this.interactions = new HashMap<>();
        this.mappings     = new HashMap<>();
    }

    /**
     * Create an {@link URI} matching this {@link InteractionContainer}.
     *
     * @param path
     *         The path without schema.
     *
     * @return An {@link URI}.
     */
    @Override
    public URI createURI(String path) {

        return URI.create(String.format("%s://%s", this.getInteractionSchema(), path));
    }

    /**
     * Register the provided {@link InteractionTarget} into this {@link InteractionContainer}.
     *
     * @param target
     *         The {@link InteractionTarget} to register.
     *
     * @return True if it has been registered, false otherwise.
     */
    @Override
    public boolean register(T target) {

        URI uri = this.createURI(target.getMeta().getName());

        if (this.getInteractions().containsKey(uri)) {
            return false;
        }

        this.getInteractions().put(uri, target);
        return true;
    }

    /**
     * Retrieve all {@link InteractionTarget} registered so far.
     *
     * @return A list of {@link InteractionTarget}
     */
    @Override
    public Map<URI, T> getInteractions() {

        return this.interactions;
    }

    /**
     * Try to find an {@link InteractionTarget} matching the {@link URI}.
     *
     * @param uri
     *         The {@link URI} to match.
     *
     * @return An optional {@link InteractionTarget}
     */
    @Override
    public Optional<T> resolve(URI uri) {

        return Optional.ofNullable(this.getInteractions().get(uri));
    }

    /**
     * Called when an {@link DispatchEvent} is being fired.
     *
     * @param event
     *         The {@link DispatchEvent}
     *
     * @return An object representing the interaction result.
     */
    @Override
    public Object dispatch(DispatchEvent<U> event) throws Exception {

        Optional<T> optionalTarget = this.resolve(event.getPath());

        if (optionalTarget.isEmpty()) {
            throw new InteractionNotFoundException(this, event);
        }

        T target = optionalTarget.get();
        return target.execute(event, this.getMappedClasses());
    }

    /**
     * Add a mapping class to the dependency map used as parameters injection source. Using {@link Supplier} allow to
     * lazy-load the value.
     *
     * @param clazz
     *         The class type to map.
     * @param mapper
     *         The {@link Injection} allowing a lazy-load of the mapping value.
     */
    @Override
    public <K> void addClassMapping(Class<K> clazz, Injection<U, K> mapper) {

        this.getMappedClasses().put(clazz, mapper);
    }

    /**
     * Retrieve all mapping used for the parameter injection.
     *
     * @return A map.
     */
    @Override
    public Map<Class<?>, Injection<U, ?>> getMappedClasses() {

        return this.mappings;
    }

    /**
     * Handle the provided event and wrap it in a {@link DispatchEvent}.
     *
     * @param event
     *         The Discord event
     *
     * @return A {@link DispatchEvent}.
     */
    @Override
    public DispatchEvent<U> handle(ITimedAction timedAction, U event) {

        return new DispatchEvent<>(timedAction, this.getEventUri(event), event);
    }
}
