package fr.alexpado.jda.interactions.interfaces;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionManager;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;

import java.util.Map;
import java.util.function.Function;

public interface ExecutableItem {

    /**
     * Execute this {@link ExecutableItem} with the provided parameters.
     *
     * @param event
     *         The {@link DispatchEvent} that allowed to match this {@link ExecutableItem}.
     * @param mapping
     *         The dependency mapping set through {@link InteractionManager#registerMapping(Class, Function)}.
     *
     * @return An {@link InteractionResponse} implementation.
     *
     * @throws Exception
     *         Threw if something happen during the execution. Implementation dependent.
     */
    InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<JdaInteraction, ?>> mapping) throws Exception;

}
