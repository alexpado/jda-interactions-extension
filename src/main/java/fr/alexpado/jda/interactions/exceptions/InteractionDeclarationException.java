package fr.alexpado.jda.interactions.exceptions;

import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.Interaction;

import java.lang.reflect.Method;

/**
 * {@link InteractionDeclarationException} is an exception thrown when an {@link InteractionTarget} had a parameter in
 * its targeted method, but the parameter isn't annotated with {@link Param}.
 */
public class InteractionDeclarationException extends RuntimeException implements DiscordEmbeddable {

    private final Class<?> declarationClass;
    private final Method   declarationMethod;
    private final String   path;

    /**
     * Create a new instance of this {@link InteractionDeclarationException}.
     *
     * @param declarationClass
     *         The class for which the interaction has a wrong declaration.
     * @param declarationMethod
     *         The method for which the interaction has a wrong declaration.
     * @param path
     *         The path of the interaction that was being executed.
     * @param message
     *         The message describing the cause of the exception.
     */
    public InteractionDeclarationException(Class<?> declarationClass, Method declarationMethod, String path, String message) {

        super(message);
        this.declarationClass  = declarationClass;
        this.declarationMethod = declarationMethod;
        this.path              = path;
    }

    /**
     * Retrieve the {@link Class} in which the bad declaration has been found.
     *
     * @return A {@link Class}
     */
    public Class<?> getDeclarationClass() {

        return this.declarationClass;
    }

    /**
     * Retrieve the {@link Method} in which the bad declaration has been found.
     *
     * @return A {@link Method}
     */
    public Method getDeclarationMethod() {

        return this.declarationMethod;
    }

    /**
     * Retrieve the {@link Interaction} path which was in use when the exception occurred.
     *
     * @return The path
     */
    public String getPath() {

        return this.path;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance (which may be {@code null}).
     */
    @Override
    public String getMessage() {

        return String.format("[%s] (%s::%s) %s", this.getPath(), this.getDeclarationClass().getSimpleName(), this
                .getDeclarationMethod().getName(), super.getMessage());
    }

    /**
     * Retrieve an {@link EmbedBuilder} representing this {@link DiscordEmbeddable}.
     *
     * @return An {@link EmbedBuilder}.
     */
    @Override
    public EmbedBuilder asEmbed() {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(super.getMessage());
        builder.addField("Class", this.declarationClass.getSimpleName(), false);
        builder.addField("Method", this.declarationMethod.getName(), false);
        builder.addField("Path", this.path, false);

        return builder;
    }

    @Override
    public boolean showToEveryone() {

        return false;
    }

}
