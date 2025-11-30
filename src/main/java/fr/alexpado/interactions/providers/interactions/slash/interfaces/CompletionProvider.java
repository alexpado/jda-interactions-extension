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
     * Indicates whether the results returned by {@link #complete(Request)} are already filtered based on the user's
     * input.
     * <p>
     * If this returns {@code true}, the framework assumes the implementation has already performed the necessary
     * filtering (e.g., via a specific database query or search API) and will use the results exactly as provided.
     * <p>
     * If this returns {@code false} (default), the framework will automatically filter the returned stream to only keep
     * choices where the name or value contains the user's current input (case-insensitive).
     *
     * @return {@code true} if the results are pre-filtered, {@code false} if the framework should apply default
     *         filtering.
     */
    default boolean isFiltered() {

        return false;
    }

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
