package fr.alexpado.jda.interactions.interfaces.interactions.button;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.MetaContainer;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

/**
 * Interface representing an {@link InteractionTarget} dedicated to {@link ButtonInteraction} events.
 */
public interface ButtonInteractionTarget extends InteractionTarget<ButtonInteraction>, MetaContainer {
}
