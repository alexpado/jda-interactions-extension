package fr.alexpado.jda.interactions.enums;

import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.function.Predicate;

public enum InteractionType {

    ALL("all://", ev -> true),
    SLASH("slash://", ev -> ev instanceof CommandInteraction),
    BUTTON("button://", ev -> ev instanceof ButtonInteraction),
    NONE("none://", ev -> false);

    private final String                 prefix;
    private final Predicate<Interaction> filter;

    InteractionType(String prefix, Predicate<Interaction> filter) {

        this.prefix = prefix;
        this.filter = filter;
    }

    public boolean isCompatible(Interaction interaction) {

        return this.filter.test(interaction);
    }

    public String getPrefix() {

        return this.prefix;
    }

    public String withPrefix(String path) {

        if (path.startsWith("/")) {
            return this.prefix + path.substring(1);
        }
        return this.prefix + path;
    }
}
