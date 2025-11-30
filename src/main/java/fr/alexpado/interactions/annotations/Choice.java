package fr.alexpado.interactions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Choice {

    /**
     * The value that will be sent as argument to the command.
     *
     * @return A value.
     */
    String value();

    /**
     * The label that will be displayed to the user representing this choice.
     *
     * @return A label.
     */
    String label();

}
