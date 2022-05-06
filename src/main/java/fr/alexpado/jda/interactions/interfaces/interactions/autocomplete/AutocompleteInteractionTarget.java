package fr.alexpado.jda.interactions.interfaces.interactions.autocomplete;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.MetaContainer;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.function.Supplier;

/**
 * Interface representing an {@link InteractionTarget} dedicated to {@link CommandAutoCompleteInteraction} events.
 */
public interface AutocompleteInteractionTarget extends InteractionTarget<CommandAutoCompleteInteraction>, MetaContainer {

    /**
     * Add an option choice generator.
     *
     * @param name
     *         The name of the option
     * @param provider
     *         The {@link Supplier} giving the list of autocompletion possible.
     */
    void addCompletionProvider(String name, AutoCompleteProvider provider);

    /**
     * Remove the completion provider for the provided option name.
     *
     * @param name
     *         The name of the option
     */
    void removeCompletionProvider(String name);

}
