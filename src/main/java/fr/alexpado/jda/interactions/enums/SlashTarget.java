package fr.alexpado.jda.interactions.enums;

import net.dv8tion.jda.api.interactions.Interaction;

import java.util.function.Predicate;

public enum SlashTarget {

    ALL(event -> true),
    GUILD(Interaction::isFromGuild),
    PRIVATE(event -> !event.isFromGuild());

    final Predicate<Interaction> compatibilityChecker;

    SlashTarget(Predicate<Interaction> compatibilityChecker) {

        this.compatibilityChecker = compatibilityChecker;
    }

    public boolean isCompatible(Interaction event) {

        return this.compatibilityChecker.test(event);
    }
}
