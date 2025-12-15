package fr.alexpado.jda.interactions.interfaces.interactions.autocomplete;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.List;

/**
 * Interface representing an object capable of providing auto-completion.
 */
@Deprecated
public interface AutoCompleteProvider {

    /**
     * Complete the focused option.
     *
     * @param event
     *         The {@link DispatchEvent} context in which the completion is required.
     * @param name
     *         The name of the option that need to be auto-completed.
     * @param completionName
     *         The completion name used when auto-completed.
     * @param value
     *         The value of the option that need to be auto-completed.
     *
     * @return A {@link List} of {@link Command.Choice}.
     */
    List<Command.Choice> complete(DispatchEvent<CommandAutoCompleteInteraction> event, String name, String completionName, String value);

}
