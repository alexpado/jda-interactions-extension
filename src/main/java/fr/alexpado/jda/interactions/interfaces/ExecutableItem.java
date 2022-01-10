package fr.alexpado.jda.interactions.interfaces;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import net.dv8tion.jda.api.interactions.Interaction;

import java.util.Map;
import java.util.function.Function;

public interface ExecutableItem {

    InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<Interaction, ?>> mapping) throws Exception;

}
