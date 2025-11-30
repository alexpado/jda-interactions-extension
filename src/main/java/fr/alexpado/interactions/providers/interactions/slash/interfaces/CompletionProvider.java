package fr.alexpado.interactions.providers.interactions.slash.interfaces;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.stream.Stream;

public interface CompletionProvider {

    Stream<Command.Choice> complete(Request<CommandAutoCompleteInteraction> request);

}
