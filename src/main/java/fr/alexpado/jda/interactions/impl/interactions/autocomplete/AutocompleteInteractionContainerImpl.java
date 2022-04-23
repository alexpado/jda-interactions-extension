package fr.alexpado.jda.interactions.impl.interactions.autocomplete;

import fr.alexpado.jda.interactions.impl.interactions.DefaultInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.net.URI;

/**
 * Class implementing {@link InteractionContainer} handling {@link CommandAutoCompleteInteraction} with target of type
 * {@link AutocompleteInteractionTarget}.
 */
public class AutocompleteInteractionContainerImpl extends DefaultInteractionContainer<AutocompleteInteractionTarget, CommandAutoCompleteInteraction> implements AutocompleteInteractionContainer {

    /**
     * Retrieve the schema for the {@link URI} of each {@link InteractionTarget}.
     *
     * @return The schema
     */
    @Override
    public String getInteractionSchema() {

        return "completion";
    }

    /**
     * Generate the {@link URI} for the provided {@link Interaction}.
     *
     * @param event
     *         The Discord event
     *
     * @return An {@link URI}
     */
    @Override
    public URI getEventUri(CommandAutoCompleteInteraction event) {

        return this.createURI(event.getCommandPath());
    }
}
