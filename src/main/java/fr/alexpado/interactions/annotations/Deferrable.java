package fr.alexpado.interactions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a handler method as requiring a deferred reply.
 * <p>
 * When an interaction is deferred, the bot will display a "Thinking..." state to the user. This is necessary for
 * operations taking longer than 3 seconds to prevent the interaction token from expiring.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Deferrable {

    /**
     * Determines if the deferred reply should be ephemeral (visible only to the user).
     *
     * @return {@code true} if the reply is ephemeral, {@code false} otherwise.
     */
    boolean ephemeral() default false;

}
