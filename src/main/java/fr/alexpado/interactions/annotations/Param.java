package fr.alexpado.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method parameter should be resolved using a value found in the request payload (Interaction data).
 * <p>
 * For Slash commands, this maps to an Option name. For Buttons, this maps to a Query Parameter key.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * The key of the parameter to retrieve.
     *
     * @return The parameter key.
     */
    String value();

}
