package fr.alexpado.interactions.providers;

import fr.alexpado.interactions.annotations.Attribute;
import fr.alexpado.interactions.exceptions.InteractionException;
import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.jda.interactions.annotations.Param;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A generic {@link RouteHandler} implementation that invokes a Java {@link Method} using reflection.
 * <p>
 * This handler automatically maps {@link Request} data to the method parameters:
 * <ul>
 *     <li>Parameters annotated with {@link Param} are resolved from {@link Request#getParameters()}.</li>
 *     <li>Parameters annotated with {@link Attribute} are resolved from {@link Request#getAttributes()}.</li>
 *     <li>Parameters of type {@link Request}, {@link Interaction}, or {@link java.net.URI} are injected from the context.</li>
 * </ul>
 *
 * @param <T>
 *         The type of interaction this handler supports.
 */
public class ReflectiveRouteHandler<T extends Interaction> implements RouteHandler<T> {

    private final Object instance;
    private final Method method;

    /**
     * Creates a new reflective handler.
     *
     * @param instance
     *         The object instance to invoke the method on.
     * @param method
     *         The method to invoke.
     */
    public ReflectiveRouteHandler(Object instance, Method method) {

        if (method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalArgumentException(String.format(
                    "Method %s in %s returns void. Interaction handlers must return something (non-void & non-null).",
                    method.getName(),
                    instance.getClass().getName()
            ));
        }

        this.instance = instance;
        this.method   = method;
    }

    /**
     * Retrieve the {@link Method} targeted by this {@link ReflectiveRouteHandler}
     *
     * @return A {@link Method}
     */
    public Method getMethod() {

        return this.method;
    }

    @Override
    public @NotNull Object handle(@NotNull Request<T> request) {

        Collection<Object> args = new ArrayList<>();

        for (Parameter parameter : this.method.getParameters()) {
            args.add(this.resolveParameter(parameter, request));
        }

        Object result;

        try {
            result = this.method.invoke(this.instance, args.toArray());
        } catch (Exception e) {
            throw new InteractionException("Failed to invoke reflective route handler", e);
        }

        if (result == null) {
            throw new InteractionException("Route handler returned null, which is not allowed.");
        }

        return result;
    }

    private Object resolveParameter(Parameter parameter, Request<T> request) {

        // Context injection
        if (Request.class.isAssignableFrom(parameter.getType())) {
            return request;
        }
        if (Interaction.class.isAssignableFrom(parameter.getType())) {
            return request.getEvent();
        }

        if (parameter.isAnnotationPresent(Param.class)) {
            String key = parameter.getAnnotation(Param.class).value();
            return this.convertType(request.getParameters().get(key), parameter.getType());
        }

        if (parameter.isAnnotationPresent(Attribute.class)) {
            String key = parameter.getAnnotation(Attribute.class).value();
            return request.getAttributes().get(key);
        }

        // As last resort, try to get an attachment from it.
        return request.getAttachment(parameter.getType());
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private Object convertType(Object input, Class<?> targetType) {

        if (input == null) return null;

        if (input instanceof String stringVal) {
            if (targetType == Long.class || targetType == long.class) return Long.parseLong(stringVal);
            if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(stringVal);
            if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(stringVal);
            if (targetType == Double.class || targetType == double.class) return Double.parseDouble(stringVal);

            if (targetType.isEnum()) {
                try {
                    //noinspection unchecked,rawtypes
                    return Enum.valueOf((Class<Enum>) targetType, stringVal);
                } catch (IllegalArgumentException e) {
                    throw new InteractionException("Invalid enum constant: " + stringVal + " for type " + targetType.getSimpleName());
                }
            }
        }

        return input;
    }

}
