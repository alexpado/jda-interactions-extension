package fr.alexpado.interactions.annotations;

import fr.alexpado.interactions.providers.interactions.slash.interfaces.CompletionProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Completion {

    /**
     * Provide completion using a named {@link CompletionProvider}.
     *
     * @return The name of the {@link CompletionProvider}.
     */
    String named() default "";

    /**
     * Provide completion using static choices.
     *
     * @return An array of {@link Choice}.
     */
    Choice[] choices() default {};

}
