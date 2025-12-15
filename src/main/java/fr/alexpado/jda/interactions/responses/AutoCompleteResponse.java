package fr.alexpado.jda.interactions.responses;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.List;

/**
 * Interface representing the default response for a {@link CommandAutoCompleteInteraction}.
 */
@Deprecated
public interface AutoCompleteResponse {

    /**
     * Retrieve the {@link List} of {@link Command.Choice} that should be sent as response.
     *
     * @return A {@link List} of {@link Command.Choice}.
     */
    List<Command.Choice> getChoices();

}
