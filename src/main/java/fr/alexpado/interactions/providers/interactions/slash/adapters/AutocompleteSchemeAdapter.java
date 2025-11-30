package fr.alexpado.interactions.providers.interactions.slash.adapters;

import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.BaseRequest;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Optional;

/**
 * Handles {@link CommandAutoCompleteInteraction}.
 * <p>
 * Routes to {@code completion://full/command/path?option=focusedOptionName}.
 */
public class AutocompleteSchemeAdapter implements SchemeAdapter<CommandAutoCompleteInteraction> {

    @Override
    public Optional<Request<CommandAutoCompleteInteraction>> createRequest(@NotNull CommandAutoCompleteInteraction event) {

        String            path    = event.getFullCommandName().replace(" ", "/");
        AutoCompleteQuery focused = event.getFocusedOption();

        URI uri = URI.create("completion://" + path + "/" + focused.getName());

        Request<CommandAutoCompleteInteraction> request = new BaseRequest<>(event, uri);

        event.getOptions().forEach(opt -> {
            if (!opt.getName().equals(focused.getName())) {
                request.getParameters().put(opt.getName(), opt.getAsString());
            }
        });

        SchemeAdapter.buildAttachments(request);
        request.addAttachment(MessageChannel.class, event.getChannel());

        return Optional.of(request);
    }

}
