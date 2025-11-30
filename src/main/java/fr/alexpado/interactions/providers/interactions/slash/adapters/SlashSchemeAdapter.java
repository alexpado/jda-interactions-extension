package fr.alexpado.interactions.providers.interactions.slash.adapters;

import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.BaseRequest;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Optional;

public class SlashSchemeAdapter implements SchemeAdapter<SlashCommandInteraction> {

    @Override
    public Optional<Request<SlashCommandInteraction>> createRequest(@NotNull SlashCommandInteraction event) {

        String path = event.getFullCommandName().replace(" ", "/");
        URI    uri  = URI.create("slash://" + path);

        Request<SlashCommandInteraction> request = new BaseRequest<>(event, uri);

        for (OptionMapping option : event.getOptions()) {
            request.getParameters().put(option.getName(), this.extractValue(option));
        }

        SchemeAdapter.buildAttachments(request);
        request.addAttachment(MessageChannel.class, event.getChannel());

        return Optional.of(request);
    }

    private Object extractValue(OptionMapping mapping) {

        return switch (mapping.getType()) {
            case STRING -> mapping.getAsString();
            case BOOLEAN -> mapping.getAsBoolean();
            case INTEGER -> mapping.getAsLong();
            case USER -> mapping.getAsUser();
            case CHANNEL -> mapping.getAsChannel();
            case ROLE -> mapping.getAsRole();
            case NUMBER -> mapping.getAsDouble();
            case ATTACHMENT -> mapping.getAsAttachment();
            case MENTIONABLE -> mapping.getAsMentionable();
            default -> throw new IllegalArgumentException("Unknown option type " + mapping.getType().name());
        };
    }

}
