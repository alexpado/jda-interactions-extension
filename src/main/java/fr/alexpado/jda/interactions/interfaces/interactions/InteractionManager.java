package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.InteractionManagerImpl;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.executors.BasicDiscordContainer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.function.Function;

public interface InteractionManager extends InteractionContainer {

    static <T extends BasicDiscordContainer> InteractionManager using(JDA builder, InteractionErrorHandler defaultErrorHandler) {

        return new InteractionManagerImpl(builder, defaultErrorHandler);
    }

    /**
     * Register a custom class injection when calling method annotated with {@link Interact}. Parameters will be injected using
     * mapping defined with this method.
     *
     * @param target
     *         The target class for the injection.
     * @param getter
     *         The function that allows converting an {@link Interaction} to the given class.
     * @param <T>
     *         The class type.
     */
    <T> void registerMapping(Class<T> target, Function<Interaction, T> getter);

    /**
     * Build the underlying {@link InteractionContainer}s globally across all guilds.
     *
     * @param jda
     *         The {@link JDA} instance to use.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    CommandListUpdateAction build(JDA jda);

    /**
     * Build the underlying {@link InteractionContainer}s only on the provided guild.
     *
     * @param guild
     *         The {@link Guild} instance to use.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    CommandListUpdateAction build(Guild guild);

    /**
     * Dispatch and execute the appropriate actions associated with the provided {@link DispatchEvent}.
     *
     * @param event
     *         A {@link DispatchEvent}.
     */
    void dispatch(DispatchEvent event);

    /**
     * Add a new {@link InteractionExecutor} to this {@link InteractionManager}.
     *
     * @param executor
     *         The {@link InteractionExecutor} instance.
     */
    void addExecutor(InteractionExecutor executor);

    /**
     * Add a new {@link InteractionResponseHandler} to this {@link InteractionManager}.
     *
     * @param responseHandler
     *         The {@link InteractionResponseHandler} instance.
     */
    void addResponseHandler(InteractionResponseHandler responseHandler);

}
