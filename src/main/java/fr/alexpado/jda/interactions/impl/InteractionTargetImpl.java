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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Class implementing the default behavior of an {@link InteractionTarget}.
 *
 * @param <T>
 *         The type of the interaction
 */
public class InteractionTargetImpl<T extends Interaction> implements InteractionTarget<T>, MetaContainer {

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
    public Object execute(DispatchEvent<T> event, Map<Class<?>, Injection<T, ?>> mapping) throws Exception {

        Collection<Object> callParameters = new ArrayList<>();

        for (Parameter parameter : this.method.getParameters()) {
            String  name        = parameter.getName();
            String  type        = parameter.getType().getSimpleName();
            boolean isOption    = parameter.isAnnotationPresent(Param.class);
            boolean isInjection = mapping.containsKey(parameter.getType());

            LOGGER.debug("Parameter {} is type {} (Option: {}, Injection: {})", name, type, isOption, isInjection);

            if (isOption) {
                Param param = parameter.getAnnotation(Param.class);
                callParameters.add(event.getOptions().get(param.value()));
            } else if (isInjection) {
                Injection<T, ?> injection = mapping.get(parameter.getType());
                try {
                    callParameters.add(injection.inject(event.getInteraction()).get());
                } catch (Exception e) {
                    throw new InteractionInjectionException(e, this.instance.getClass(), this.method, parameter);
                }
            } else {
                throw new InteractionDeclarationException(this.instance.getClass(), this.method, this.meta.getName(), "Unmapped parameter " + type);
            }
        }

        try {
            return this.method.invoke(this.instance, callParameters.toArray(Object[]::new));
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
}
