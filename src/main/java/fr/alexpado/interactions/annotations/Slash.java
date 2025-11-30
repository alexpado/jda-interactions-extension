package fr.alexpado.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a Slash Command handler.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Slash {

    /**
     * The command path (e.g., "command", "command/subcommand", "group/subgroup/command").
     *
     * @return The command path.
     */
    String name();

    /**
     * Description of the command.
     *
     * @return The description.
     */
    String description();

    /**
     * Options of the command.
     *
     * @return The options.
     */
    Option[] options() default {};

}
