package fr.alexpado.jda.interactions.annotations;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Option {

    String name();

    String description();

    Choice[] choices() default {};

    /**
     * Define if this option is required for the command to be executed. The verification is done by Discord.
     *
     * @return True if the option is required, false otherwise.
     */
    boolean required() default false;

    /**
     * The type of the option. This will affect this option auto-complete behaviour in the Discord client.
     *
     * <ul>
     *     <li>{@link OptionType#STRING}: Allow the user to type any text. (Parameter type is {@link String})</li>
     *     <li>{@link OptionType#INTEGER}: Allow the user to type any number. (Parameter type is {@link Long})</li>
     *     <li>{@link OptionType#BOOLEAN}: Allow the user to type only in a yes/no fashion. (Parameter type is {@link Boolean})</li>
     *     <li>{@link OptionType#USER}: Allow the user to select another user. (Parameter type is {@link Member})</li>
     *     <li>{@link OptionType#CHANNEL}: Allow the user to select a channel. On the client, this include both {@link VoiceChannel}, {@link TextChannel} and {@link Category}. (Parameter type is {@link GuildChannel})</li>
     *     <li>{@link OptionType#ROLE}: Allow the user to select a role. (Parameter type is {@link Role})</li>
     *     <li>{@link OptionType#MENTIONABLE}: Allow the user to select anything that can be mentioned. (Parameter type is {@link IMentionable})</li>
     * </ul>
     *
     * @return The option's type.
     */
    OptionType type();

}
