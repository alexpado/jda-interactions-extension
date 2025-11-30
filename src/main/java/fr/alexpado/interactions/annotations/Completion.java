package fr.alexpado.interactions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Completion {

    String named() default "";

    Choice[] choices() default {};

}
