package fr.alexpado.jda.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Annotation used to mark a {@link Method} {@link Parameter} as one receiving an {@link Option} value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Param {

    /**
     * The name of the associated {@link Option}.
     *
     * @return The {@link Option}'s name.
     */
    String value();

}
