package fr.alexpado.jda.interactions.annotations;

import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation allowing to list all options available for an {@link Interact}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Option {

    /**
     * The name of the option. In a slash context, this will be shown as command parameter.
     *
     * @return The option's name.
     */
    String name();

    /**
     * The description of the option. In a Slash context, this will be shown as a command parameter description.
     *
     * @return The option's description.
     */
    String description();

    /**
     * List of {@link Choice} available for this option.
     *
     * @return The option's choices.
     */
    Choice[] choices() default {};

    /**
     * Define if this option is required for the interaction to be executed. In a Slash context, the verification is done by
     * Discord.
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
     *     <li>{@link OptionType#CHANNEL}: Allow the user to select a channel. (Parameter type is {@link GuildChannelUnion})</li>
     *     <li>{@link OptionType#ROLE}: Allow the user to select a role. (Parameter type is {@link Role})</li>
     *     <li>{@link OptionType#MENTIONABLE}: Allow the user to select anything that can be mentioned. (Parameter type is {@link IMentionable})</li>
     * </ul>
     *
     * @return The option's type.
     */
    OptionType type();

}
