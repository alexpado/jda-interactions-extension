package fr.alexpado.jda.interactions.interfaces.interactions.slash;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.MetaContainer;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

/**
 * Interface representing an {@link InteractionTarget} dedicated to {@link SlashCommandInteraction} events.
 */
@Deprecated
public interface SlashInteractionTarget extends InteractionTarget<SlashCommandInteraction>, MetaContainer {
}
