package fr.alexpado.jda.interactions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation allowing to list all choices available for an {@link Option}.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Choice {

    /**
     * The choice id. This will be sent back to your interaction.
     *
     * @return The choice's id.
     */
    String id();

    /**
     * The choice display text. This will be shown in the Discord client.
     *
     * @return The choice's display text.
     */
    String display();

}
