package fr.alexpado.interactions.annotations;

import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method parameter should be resolved using a value found in the request attributes.
 * <p>
 * The key specified in {@link #value()} will be used to look up the object in the attribute map.
 * <p>
 * <b>Note:</b> Without proper handling in a {@link RouteHandler}, this annotation will have no effect.
 *
 * @see Request#getAttributes()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Attribute {

    /**
     * The key of the attribute to retrieve.
     *
     * @return The attribute key.
     */
    String value();

}
