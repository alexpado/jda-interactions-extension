package fr.alexpado.jda.interactions.enums;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.function.Predicate;

/**
 * Enum allowing to define the target for an {@link InteractionTarget}.
 */
@Deprecated
public enum SlashTarget {

    /**
     * The {@link InteractionTarget} can be executed in any context.
     */
    ALL(event -> true),
    /**
     * The {@link InteractionTarget} can be executed only in Guilds.
     */
    GUILD(Interaction::isFromGuild),
    /**
     * The {@link InteractionTarget} can be executed only in Private Channels.
     */
    PRIVATE(event -> !event.isFromGuild());

    final Predicate<Interaction> compatibilityChecker;

    /**
     * Create a new {@link SlashTarget} enum.
     *
     * @param compatibilityChecker
     *         A {@link Predicate} testing the compatibility between the {@link SlashTarget} and an {@link Interaction}
     */
    SlashTarget(Predicate<Interaction> compatibilityChecker) {

        this.compatibilityChecker = compatibilityChecker;
    }

    /**
     * Check if the current {@link SlashTarget} is compatible with the provided {@link Interaction}.
     *
     * @param event
     *         The {@link Interaction} to check
     *
     * @return True if compatible, false otherwise.
     */
    public boolean isCompatible(Interaction event) {

        return this.compatibilityChecker.test(event);
    }
}
