package fr.alexpado.jda.interactions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Choice {

    String id();

    String display();

}
