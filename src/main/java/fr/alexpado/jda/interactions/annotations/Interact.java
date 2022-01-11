package fr.alexpado.jda.interactions.annotations;

import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Annotation allowing to mark a {@link Method} as a target for a Discord Interaction.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Interact {

    /**
     * The name of this interaction. In most cases a path.
     *
     * @return The interaction's name.
     */
    String name();

    /**
     * The description of this interaction.
     *
     * @return The interaction's description.
     */
    String description();

    /**
     * The type to which this interaction is a target.
     *
     * @return The interaction's type.
     */
    InteractionType type() default InteractionType.NONE;

    /**
     * The context in which this interaction can be used.
     *
     * @return The interaction's target.
     */
    SlashTarget target() default SlashTarget.ALL;

    /**
     * List of {@link Option} available for this interaction.
     *
     * @return The interaction's options.
     */
    Option[] options() default {};

    /**
     * Define whether the result should be shown only for the user using this interaction only when the interaction is a
     * {@link SlashCommandEvent}.
     *
     * @return True if private, false otherwise.
     */
    boolean hideAsSlash() default false;

    /**
     * Define whether the result should be shown only for the user using this interaction only when the interaction is a
     * {@link ButtonClickEvent}.
     *
     * @return True if private, false otherwise.
     */
    boolean hideAsButton() default false;

    /**
     * Define if the result should be done asynchronously and tell Discord that we'll respond to the interaction later.
     *
     * @return True if it should be deferred, false otherwise.
     */
    boolean defer() default false;

}
