package fr.alexpado.jda.interactions.exceptions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.Interaction;

/**
 * Exception thrown when an {@link InteractionContainer} can't find the associated {@link InteractionTarget} to the
 * provided {@link DispatchEvent}.
 *
 * @see InteractionContainer#dispatch(DispatchEvent)
 */
@Deprecated
public class InteractionNotFoundException extends RuntimeException implements DiscordEmbeddable {

    private final InteractionContainer<?, ?> container;
    private final DispatchEvent<?>           event;

    /**
     * Create a new {@link InteractionNotFoundException} with the provided {@link InteractionContainer} and
     * {@link DispatchEvent}.
     *
     * @param container
     *         The {@link InteractionContainer} that was handling the {@link Interaction} but couldn't find the
     *         associated {@link InteractionTarget}.
     * @param event
     *         The {@link DispatchEvent} that was given to the {@link InteractionContainer}.
     * @param <T>
     *         The type of the {@link Interaction}.
     */
    public <T extends Interaction> InteractionNotFoundException(InteractionContainer<?, T> container, DispatchEvent<T> event) {

        super(String.format("Interaction '%s' not found in container '%s'", event.path(), container.getClass()
                                                                                                   .getName()));
        this.container = container;
        this.event     = event;
    }

    /**
     * Retrieve the {@link DispatchEvent} for which no {@link InteractionTarget} was found.
     *
     * @return A {@link DispatchEvent}
     */
    public DispatchEvent<?> getEvent() {

        return this.event;
    }

    /**
     * Retrieve the {@link InteractionContainer} in which the {@link InteractionTarget} was not found.
     *
     * @return An {@link InteractionContainer}
     */
    public InteractionContainer<?, ?> getContainer() {

        return this.container;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Unable to find the requested interaction");
        builder.setDescription("Please check your declaration and make sure that you registered your interaction target.");

        builder.addField("URI", "`%s`".formatted(this.event.path()), false);
        builder.addField("Context", this.event.interaction().getClass().getSimpleName(), false);
        builder.addField("Container", this.container.getClass().getSimpleName(), false);
        builder.addField("Options", String.valueOf(this.event.options().size()), false);

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
