package fr.alexpado.jda.interactions.interfaces.interactions.slash;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionEventHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

/**
 * Interface representing an {@link InteractionContainer} dedicated to {@link SlashCommandInteraction} events.
 */
public interface SlashInteractionContainer extends InteractionContainer<SlashInteractionTarget, SlashCommandInteraction>, InteractionEventHandler<SlashCommandInteraction>, InteractionResponseHandler {

    /**
     * Convert and insert all {@link SlashInteractionTarget} into the {@link CommandListUpdateAction} provided and
     * returns it.
     *
     * @param action
     *         The {@link CommandListUpdateAction} into which commands will be inserted.
     *
     * @return The updated {@link CommandListUpdateAction}.
     */
    CommandListUpdateAction upsertCommands(CommandListUpdateAction action);

}
