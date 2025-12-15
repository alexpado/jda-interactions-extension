package fr.alexpado.jda.interactions.impl.interactions.button;

import fr.alexpado.jda.interactions.impl.InteractionTargetImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.lang.reflect.Method;

/**
 * Class implementing {@link InteractionTarget} being the execution target of {@link ButtonInteraction}.
 */
@Deprecated
public class ButtonInteractionTargetImpl extends InteractionTargetImpl<ButtonInteraction> implements ButtonInteractionTarget {

    /**
     * Create a new {@link SlashInteractionTarget} implementation instance.
     *
     * @param instance
     *         The instance object within which the interaction exists.
     * @param method
     *         The method to use when executing the interaction.
     * @param meta
     *         The meta representing this {@link SlashInteractionTarget}.
     */
    public ButtonInteractionTargetImpl(Object instance, Method method, InteractionMeta meta) {

        super(instance, method, meta);
    }
}
