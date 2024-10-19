package fr.alexpado.jda.interactions.impl;

import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.exceptions.InteractionDeclarationException;
import fr.alexpado.jda.interactions.exceptions.InteractionInjectionException;
import fr.alexpado.jda.interactions.interfaces.interactions.Injection;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.MetaContainer;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Class implementing the default behavior of an {@link InteractionTarget}.
 *
 * @param <T>
 *         The type of the interaction
 */
public class InteractionTargetImpl<T extends Interaction> implements InteractionTarget<T>, MetaContainer {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<>() {{
        this.put(long.class, Long.class);
        this.put(boolean.class, Boolean.class);
        this.put(double.class, Double.class);
    }};

    private final static Logger LOGGER = LoggerFactory.getLogger(InteractionTargetImpl.class);

    private final Object          instance;
    private final Method          method;
    private final InteractionMeta meta;

    /**
     * Create a new {@link InteractionTargetImpl} implementation instance.
     *
     * @param instance
     *         The instance object within which the interaction exists.
     * @param method
     *         The method to use when executing the interaction.
     * @param meta
     *         The meta representing this {@link InteractionTargetImpl}.
     */
    public InteractionTargetImpl(Object instance, Method method, InteractionMeta meta) {

        this.instance = instance;
        this.method   = method;
        this.meta     = meta;
    }

    /**
     * Run this {@link InteractionTarget}.
     *
     * @param event
     *         The event responsible for this execution.
     * @param mapping
     *         Map of dependencies used for the parameter injection.
     *
     * @return An {@link Object} representing the result of the execution. The result can be used directly without any
     *         result handler.
     *
     * @throws Exception
     *         If the execution could not occur, or due to an userland exception defined in the interaction.
     * @see InteractionResponseHandler
     */
    @Override
    public Object execute(DispatchEvent<T> event, Map<Class<?>, Injection<DispatchEvent<T>, ?>> mapping) throws Exception {

        if (this.getMeta().isDeferred()) {
            event.getTimedAction().action("deferring", "Deferring the interaction");
            boolean     reply       = this.getMeta().shouldReply();
            Interaction interaction = event.getInteraction();

            if (reply && interaction instanceof IReplyCallback callback) {
                callback.deferReply(this.getMeta().isHidden()).complete();
            } else if (!reply && interaction instanceof IMessageEditCallback callback) {
                callback.deferEdit().complete();
            } else {
                throw new UnsupportedOperationException("Couldn't pre-handle deferred request");
            }
            event.getTimedAction().endAction();
        }

        event.getTimedAction().action("injection", "Injecting parameters");
        Collection<Object> callParameters = new ArrayList<>();

        for (Parameter parameter : this.method.getParameters()) {
            event.getTimedAction().action("param", "Injecting " + parameter.getName());
            String  name        = parameter.getName();
            String  type        = parameter.getType().getSimpleName();
            boolean isOption    = parameter.isAnnotationPresent(Param.class);
            boolean isInjection = mapping.containsKey(parameter.getType());

            LOGGER.debug("Parameter {} is type {} (Option: {}, Injection: {})", name, type, isOption, isInjection);

            @Nullable
            Object parameterInput;

            if (isOption) {
                Param  param = parameter.getAnnotation(Param.class);
                Object obj   = event.getOptions().get(param.value());

                if (!this.canMap(parameter, obj) && isInjection) { // Special case where the injection is used as converter
                    Injection<DispatchEvent<T>, ?> injection = mapping.get(parameter.getType());
                    Supplier<?>                    injecter  = injection.inject(event, param.value());

                    try {
                        parameterInput = injecter.get();
                    } catch (Exception e) {
                        throw new InteractionInjectionException(e, this.instance.getClass(), this.method, parameter);
                    }
                } else {
                    parameterInput = obj;
                }
            } else if (isInjection) {
                Injection<DispatchEvent<T>, ?> injection = mapping.get(parameter.getType());
                Supplier<?>                    injecter  = injection.inject(event, null);

                try {
                    parameterInput = injecter.get();
                } catch (Exception e) {
                    throw new InteractionInjectionException(e, this.instance.getClass(), this.method, parameter);
                }
            } else {
                throw new InteractionDeclarationException(
                        this.instance.getClass(),
                        this.method,
                        this.meta.getName(),
                        "Unmapped parameter " + type
                );
            }

            this.checkMapping(parameter, parameterInput);
            callParameters.add(parameterInput);
            event.getTimedAction().endAction();
        }
        event.getTimedAction().endAction();

        try {
            event.getTimedAction().action("invoke", "Running the interaction");
            Object result = this.method.invoke(this.instance, callParameters.toArray(Object[]::new));
            event.getTimedAction().endAction();
            return result;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception ex) {
                throw ex;
            }
            throw e;
        }
    }

    /**
     * Retrieve the {@link InteractionMeta} of this {@link MetaContainer}.
     *
     * @return The {@link InteractionMeta} instance.
     */
    @Override
    public InteractionMeta getMeta() {

        return this.meta;
    }


    private boolean canMap(Parameter parameter, Object parameterInput) {

        try {
            this.checkMapping(parameter, parameterInput);
            return true;
        } catch (InteractionInjectionException e) {
            return false;
        }
    }

    private void checkMapping(Parameter parameter, Object parameterInput) throws InteractionInjectionException {
        // Sanity checks, please bear with me :(
        if (parameter.getType().isPrimitive()) {
            if (parameterInput == null) {
                throw new InteractionInjectionException(
                        this.instance.getClass(),
                        this.method,
                        parameter,
                        "Unable to assign null-value to a primitive typed parameter."
                );
            }

            if (!PRIMITIVE_MAP.containsKey(parameter.getType())) {
                throw new InteractionInjectionException(
                        this.instance.getClass(),
                        this.method,
                        parameter,
                        "Parameter is an unsupported primitive type (supported: long, boolean, double)."
                );
            }

            if (!PRIMITIVE_MAP.get(parameter.getType()).isAssignableFrom(parameterInput.getClass())) {
                throw new InteractionInjectionException(
                        this.instance.getClass(),
                        this.method,
                        parameter,
                        parameterInput
                );
            }
        } else if (parameterInput != null && !parameter.getType().isAssignableFrom(parameterInput.getClass())) {
            throw new InteractionInjectionException(
                    this.instance.getClass(),
                    this.method,
                    parameter,
                    parameterInput
            );
        }
    }
}
