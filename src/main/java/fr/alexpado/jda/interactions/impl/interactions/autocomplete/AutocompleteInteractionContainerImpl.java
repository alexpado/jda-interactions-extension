package fr.alexpado.jda.interactions.impl.interactions.autocomplete;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.impl.interactions.DefaultInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.autocomplete.AutocompleteInteractionTarget;
import fr.alexpado.jda.interactions.responses.AutoCompleteResponse;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

/**
 * Class implementing {@link InteractionContainer} handling {@link CommandAutoCompleteInteraction} with target of type
 * {@link AutocompleteInteractionTarget}.
 */
@Deprecated
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

        return this.createURI(event.getFullCommandName().replace(" ", "/"));
    }

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided response.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The object representing the response given by an interaction.
     *
     * @return True if able to handle, false otherwise.
     */
    @Override
    public <T extends Interaction> boolean canHandle(DispatchEvent<T> event, @Nullable Object response) {

        return response instanceof AutoCompleteResponse;
    }

    /**
     * Handle the response resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The {@link Object} to handle.
     */
    @Override
    public <T extends Interaction> void handleResponse(DispatchEvent<T> event, @Nullable Object response) {

        if (event.interaction() instanceof CommandAutoCompleteInteraction interaction && response instanceof AutoCompleteResponse completion) {
            event.timedAction().action("build", "Building the response");
            List<Command.Choice> choices = completion.getChoices()
                                                     .stream()
                                                     .limit(OptionData.MAX_CHOICES)
                                                     .toList();
            event.timedAction().endAction();

            event.timedAction().action("replying", "Sending the reply");
            interaction.replyChoices(choices).complete();
            event.timedAction().endAction();
        }
    }
}
