package fr.alexpado.jda.interactions.annotations;

import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.enums.SlashTarget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Interact {

    String name();

    String description();

    InteractionType type() default InteractionType.NONE;

    SlashTarget target() default SlashTarget.ALL;

    Option[] options() default {};

    boolean hideAsSlash() default false;

    boolean hideAsButton() default false;

}
