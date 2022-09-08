package fr.alexpado.jda.interactions.tools;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/**
 * Class grouping all kind of utility methods
 */
public final class InteractionUtils {

    private InteractionUtils() {}

    /**
     * Extract the value from the provided {@link OptionMapping}.
     *
     * @param option
     *         The option
     *
     * @return The extracted value
     */
    public static Object extractOptionValue(OptionMapping option) {

        return switch (option.getType()) {
            case BOOLEAN -> option.getAsBoolean();
            case STRING -> option.getAsString();
            case INTEGER -> option.getAsLong();
            case CHANNEL -> option.getAsChannel();
            case USER -> option.getAsUser();
            case ROLE -> option.getAsRole();
            case MENTIONABLE -> option.getAsMentionable();
            case NUMBER -> option.getAsDouble();
            case ATTACHMENT -> option.getAsAttachment();
            default -> null;
        };
    }

}
