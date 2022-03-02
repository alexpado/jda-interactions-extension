package fr.alexpado.jda.interactions.enums;

import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.function.Predicate;

public enum SlashTarget {

    ALL(event -> true),
    GUILD(JdaInteraction::isFromGuild),
    PRIVATE(event -> !event.isFromGuild());

    final Predicate<JdaInteraction> compatibilityChecker;

    SlashTarget(Predicate<JdaInteraction> compatibilityChecker) {

        this.compatibilityChecker = compatibilityChecker;
    }

    public boolean isCompatible(JdaInteraction event) {

        return this.compatibilityChecker.test(event);
    }
}
