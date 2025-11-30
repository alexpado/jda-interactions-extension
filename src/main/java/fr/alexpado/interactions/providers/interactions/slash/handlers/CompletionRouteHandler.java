package fr.alexpado.interactions.providers.interactions.slash.handlers;

import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.interactions.slash.interfaces.CompletionProvider;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class CompletionRouteHandler implements RouteHandler<CommandAutoCompleteInteraction> {

    private final CompletionProvider provider;

    public CompletionRouteHandler(CompletionProvider provider) {

        this.provider = provider;
    }

    @NotNull
    @Override
    public Object handle(@NotNull Request<CommandAutoCompleteInteraction> request) {

        if (this.provider.isFiltered()) {
            return this.provider.complete(request).toList();
        }

        String            current  = request.getEvent().getFocusedOption().getValue();
        Predicate<String> contains = str -> str.toLowerCase().contains(current.toLowerCase());

        return this.provider.complete(request)
                            .filter(choice -> contains.test(choice.getName()) || contains.test(choice.getAsString()))
                            .toList();
    }

}
