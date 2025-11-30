package fr.alexpado.interactions.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an option for a {@link Slash} command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Option {

    /**
     * Define this {@link Option}'s name.
     */
    String name();

    /**
     * Define this {@link Option}'s description.
     */
    String description();

    /**
     * Define this {@link Option}'s {@link OptionType}.
     */
    OptionType type();

    /**
     * Define if this {@link Option} is required.
     */
    boolean required() default false;

    /**
     * Define this {@link Option}'s completion settings.
     */
    Completion completion() default @Completion;

    /**
     * Define this {@link Option}'s minimal value allowed when the type is of {@link OptionType#INTEGER}.
     */
    long minInt() default Long.MIN_VALUE;


    /**
     * Define this {@link Option}'s maximal value allowed when the type is of {@link OptionType#INTEGER}.
     */
    long maxInt() default Long.MAX_VALUE;

    /**
     * Define this {@link Option}'s minimal value allowed when the type is of {@link OptionType#NUMBER}.
     */
    double minNum() default Double.NEGATIVE_INFINITY;

    /**
     * Define this {@link Option}'s maximal value allowed when the type is of {@link OptionType#NUMBER}.
     */
    double maxNum() default Double.POSITIVE_INFINITY;

    /**
     * Define this {@link Option}'s minimal length allowed when the type is of {@link OptionType#STRING}.
     */
    int minLength() default -1;

    /**
     * Define this {@link Option}'s maximum length allowed when the type is of {@link OptionType#STRING}.
     */
    int maxLength() default -1;

}
