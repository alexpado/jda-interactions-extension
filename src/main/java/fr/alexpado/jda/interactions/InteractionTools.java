package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.meta.ChoiceMeta;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.function.Consumer;

public class InteractionTools {

    public static void registerOptions(Consumer<OptionData> registration, InteractionMeta interact) {

        for (OptionMeta option : interact.getOptions()) {
            OptionData optionData = new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired());

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

    public static MessageEmbed asEmbed(Color color, String message) {

        return asEmbedBuilder(color, message).build();
    }

    public static EmbedBuilder asEmbedBuilder(Color color, String message) {

        return new EmbedBuilder().setColor(color).setDescription(message);
    }


}
