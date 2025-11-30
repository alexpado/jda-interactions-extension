package fr.alexpado.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a handler for a Button Interaction.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Button {

    /**
     * The URI path of the button (e.g., "my/action").
     * <p>
     * Query parameters in the actual interaction ID will be parsed into request parameters.
     *
     * @return The button URI.
     */
    String name();

}
