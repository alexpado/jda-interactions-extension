package fr.alexpado.jda.interactions.interfaces.interactions.autocomplete;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionEventHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

/**
 * Interface representing an {@link InteractionContainer} dedicated to {@link CommandAutoCompleteInteraction} events.
 */
@Deprecated
public interface AutocompleteInteractionContainer extends InteractionContainer<AutocompleteInteractionTarget, CommandAutoCompleteInteraction>, InteractionEventHandler<CommandAutoCompleteInteraction>, InteractionResponseHandler {
}
