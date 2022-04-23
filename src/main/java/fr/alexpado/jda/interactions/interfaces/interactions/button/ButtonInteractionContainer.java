package fr.alexpado.jda.interactions.interfaces.interactions.button;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionEventHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

/**
 * Interface representing an {@link InteractionContainer} dedicated to {@link ButtonInteraction} events.
 */
public interface ButtonInteractionContainer extends InteractionContainer<ButtonInteractionTarget, ButtonInteraction>, InteractionEventHandler<ButtonInteraction>, InteractionResponseHandler {
}
