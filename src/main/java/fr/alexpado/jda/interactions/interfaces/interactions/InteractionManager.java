package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.InteractionManagerImpl;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.executors.BasicDiscordContainer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.function.Function;

public interface InteractionManager extends InteractionContainer {

    static <T extends BasicDiscordContainer> InteractionManager using(JDABuilder builder, InteractionErrorHandler defaultErrorHandler) {

        return new InteractionManagerImpl(builder, defaultErrorHandler);
    }

    <T> void registerMapping(Class<T> target, Function<Interaction, T> getter);

    CommandListUpdateAction build(JDA jda);

    CommandListUpdateAction build(Guild guild);

    void dispatch(DispatchEvent event);

    void addExecutor(InteractionExecutor resolver);

    void addResponseHandler(InteractionResponseHandler responseHandler);

}
