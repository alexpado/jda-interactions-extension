package fr.alexpado.jda.interactions.impl.interactions.slash;

import fr.alexpado.jda.interactions.impl.InteractionTargetImpl;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.lang.reflect.Method;

/**
 * Class implementing {@link InteractionTarget} being the execution target of {@link SlashCommandInteraction}.
 */
@Deprecated
public class SlashInteractionTargetImpl extends InteractionTargetImpl<SlashCommandInteraction> implements SlashInteractionTarget {

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
    public SlashInteractionTargetImpl(Object instance, Method method, InteractionMeta meta) {

        super(instance, method, meta);
    }
}
