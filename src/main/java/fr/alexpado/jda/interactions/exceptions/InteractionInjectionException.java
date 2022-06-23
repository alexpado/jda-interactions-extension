package fr.alexpado.jda.interactions.exceptions;

import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import net.dv8tion.jda.api.EmbedBuilder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Exception thrown when the injecting the value for a parameter annotated with {@link Param} fails.
 */
public class InteractionInjectionException extends Exception implements DiscordEmbeddable {

    private final Class<?>  source;
    private final Method    method;
    private final Parameter parameter;

    /**
     * Create a new {@link InteractionInjectionException}.
     *
     * @param cause
     *         The {@link Exception} that caused the injection to fails.
     * @param source
     *         The {@link Class} that was used during the execution.
     * @param method
     *         The {@link Method} that was used during the execution.
     * @param parameter
     *         The {@link Parameter} for which the injection failed.
     */
    public InteractionInjectionException(Exception cause, Class<?> source, Method method, Parameter parameter) {

        super(
                String.format("Failed to inject dependency %s on %s@%s: %s",
                        parameter.getType().getTypeName(),
                        method.getName(),
                        parameter.getName(),
                        cause.getMessage()
                ), cause);

        this.source    = source;
        this.method    = method;
        this.parameter = parameter;
    }

    /**
     * Retrieve the {@link Class} for which the injection failed.
     *
     * @return A {@link Class}
     */
    public Class<?> getSource() {

        return this.source;
    }

    /**
     * Retrieve the {@link Method} for which the injection failed.
     *
     * @return A {@link Method}.
     */
    public Method getMethod() {

        return this.method;
    }

    /**
     * Retrieve the {@link Parameter} for which the injection failed.
     *
     * @return A {@link Parameter}
     */
    public Parameter getParameter() {

        return this.parameter;
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("Failed to inject dependency");

        builder.addField("Class", this.source.getSimpleName(), false);
        builder.addField("Method", this.method.getName(), false);
        builder.addField("Parameter", this.parameter.getName(), false);

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
