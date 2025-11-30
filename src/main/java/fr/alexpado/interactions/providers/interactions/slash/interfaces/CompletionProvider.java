package fr.alexpado.interactions.providers.interactions.slash.interfaces;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.stream.Stream;


/**
 * Functional interface for components capable of providing autocomplete choices for Slash Commands.
 */
public interface CompletionProvider {

    /**
     * Generates a stream of completion choices for the current interaction request.
     *
     * @param request
     *         The incoming autocomplete request containing the user's input and context.
     *
     * @return A {@link Stream} of {@link Command.Choice} objects.
     */
    Stream<Command.Choice> complete(Request<CommandAutoCompleteInteraction> request);

}
