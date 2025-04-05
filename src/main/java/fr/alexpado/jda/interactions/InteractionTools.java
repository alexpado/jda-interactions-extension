package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.meta.ChoiceMeta;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.function.Consumer;

public final class InteractionTools {

    private InteractionTools() {}

    public static void registerOptions(Consumer<OptionData> registration, InteractionMeta interact) {

        for (OptionMeta option : interact.getOptions()) {
            OptionData optionData = new OptionData(
                    option.getType(),
                    option.getName(),
                    option.getDescription(),
                    option.isRequired()
            );

            for (ChoiceMeta choice : option.getChoices()) {
                if (option.getType() == OptionType.INTEGER) {
                    optionData.addChoice(choice.getDisplay(), Integer.parseInt(choice.getId()));
                } else {
                    optionData.addChoice(choice.getDisplay(), choice.getId());
                }
            }
            registration.accept(optionData);
        }
    }

    public static MessageEmbed asEmbed(Color color, CharSequence message) {

        return asEmbedBuilder(color, message).build();
    }

    public static EmbedBuilder asEmbedBuilder(Color color, CharSequence message) {

        return new EmbedBuilder().setColor(color).setDescription(message);
    }

    public static String getInteractionPath(CommandInteractionPayload interaction) {

        StringBuilder builder = new StringBuilder(interaction.getName());
        if (interaction.getSubcommandGroup() != null) {
            builder.append('/').append(interaction.getSubcommandGroup());
        }
        if (interaction.getSubcommandName() != null) {
            builder.append('/').append(interaction.getSubcommandName());
        }

        // Remove leading space.
        return builder.substring(1);
    }


}
