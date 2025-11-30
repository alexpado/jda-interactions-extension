package fr.alexpado.interactions.annotations;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an option for a {@link Slash} command.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // Only used inside @Slash or other annotations, not on methods directly
public @interface Option {

    String name();

    String description();

    OptionType type();

    boolean required() default false;

    Completion completion() default @Completion;

    /**
     * Minimum value for {@link OptionType#INTEGER}.
     */
    long minInt() default Long.MIN_VALUE;

    /**
     * Maximum value for {@link OptionType#INTEGER}.
     */
    long maxInt() default Long.MAX_VALUE;

    /**
     * Minimum value for {@link OptionType#NUMBER}.
     */
    double minNum() default Double.NEGATIVE_INFINITY;

    /**
     * Maximum value for {@link OptionType#NUMBER}.
     */
    double maxNum() default Double.POSITIVE_INFINITY;

    /**
     * Minimum length for {@link OptionType#STRING}.
     */
    int minLength() default -1;

    /**
     * Maximum length for {@link OptionType#STRING}.
     */
    int maxLength() default -1;

}
