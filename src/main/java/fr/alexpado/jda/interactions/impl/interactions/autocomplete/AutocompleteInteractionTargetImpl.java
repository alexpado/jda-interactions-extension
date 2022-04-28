package fr.alexpado.jda.interactions.impl.interactions.autocomplete;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.MetaContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import fr.alexpado.jda.interactions.meta.ChoiceMeta;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class implementing {@link InteractionTarget} being the execution target of {@link SlashCommandInteraction}.
 */
public class AutocompleteInteractionTargetImpl implements AutocompleteInteractionTarget {

    private final InteractionMeta                         meta;
    private final Map<String, Supplier<List<ChoiceMeta>>> dynamicChoices;

    /**
     * Create a new instance of this {@link AutocompleteInteractionContainer} implementation.
     *
     * @param meta
     *         The {@link InteractionMeta} associated with this {@link MetaContainer}.
     */
    public AutocompleteInteractionTargetImpl(InteractionMeta meta) {

        this.meta           = meta;
        this.dynamicChoices = new HashMap<>();
    }

    /**
     * Run this {@link InteractionTarget}.
     *
     * @param event
     *         The event responsible for this execution.
     * @param mapping
     *         Map of dependencies used for the parameter injection.
     *
     * @return An {@link Object} representing the result of the execution. The result can be used directly without any
     *         result handler.
     *
     * @see InteractionResponseHandler
     */
    @Override
    public List<ChoiceMeta> execute(DispatchEvent<CommandAutoCompleteInteraction> event, Map<Class<?>, Injection<DispatchEvent<CommandAutoCompleteInteraction>, ?>> mapping) {

        CommandAutoCompleteInteraction interaction = event.getInteraction();
        AutoCompleteQuery              focused     = interaction.getFocusedOption();

        String     name  = focused.getName();
        String     value = focused.getValue();
        OptionType type  = focused.getType();

        if (this.dynamicChoices.containsKey(name)) {
            return this.dynamicChoices.get(name).get().stream().filter(choice -> choice.contains(value)).toList();
        }

        // Dynamic option not found, returning static default value.
        for (OptionMeta option : this.meta.getOptions()) {
            if (option.getName().equals(name)) {
                return option.getChoices().stream().filter(choice -> choice.contains(value)).toList();
            }
        }

        // Nothing to autocomplete
        return Collections.emptyList();
    }

    /**
     * Add an option choice generator.
     *
     * @param name
     *         The name of the option
     * @param generator
     *         The {@link Supplier} giving the list of autocompletion possible.
     */
    @Override
    public void addDynamicMapping(String name, Supplier<List<ChoiceMeta>> generator) {

        // Overwriting the existing one ensure to keep it always up to date.
        this.dynamicChoices.put(name, generator);
    }

    /**
     * Remove the dynamic autocompletion for the provided option name.
     *
     * @param name
     *         The name of the option
     */
    @Override
    public void removeDynamicMapping(String name) {

        this.dynamicChoices.remove(name);
    }

    /**
     * Retrieve the {@link InteractionMeta} of this {@link MetaContainer}.
     *
     * @return The {@link InteractionMeta} instance.
     */
    @Override
    public InteractionMeta getMeta() {

        return this.meta;
    }
}
