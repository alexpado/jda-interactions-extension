package fr.alexpado.jda.interactions.enums;

import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.function.Predicate;

public enum InteractionType {

    ALL("all://"),
    SLASH("slash://"),
    BUTTON("button://"),
    NONE("none://");

    private final String                    prefix;

    InteractionType(String prefix) {

        this.prefix = prefix;
    }

    public boolean isCompatible(JdaInteraction interaction) {

        return switch (this) {
            case ALL -> true;
            case SLASH, BUTTON -> interaction.getInteractionType() == this;
            default -> false;
        };
    }

    public String getPrefix() {

        return prefix;
    }

    public String withPrefix(String path) {

        return this.prefix + path;
    }
}
