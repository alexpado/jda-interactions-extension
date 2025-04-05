package fr.alexpado.jda.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.enums.InteractionType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class InteractionListener extends ListenerAdapter {

    private final InteractionManagerImpl manager;

    public InteractionListener(InteractionManagerImpl manager) {

        this.manager = manager;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        String path = event.getComponentId();
        URI    uri  = URI.create(event.getComponentId());

        if (uri.getScheme() == null) {
            uri = URI.create(InteractionType.BUTTON.withPrefix(event.getComponentId()));
        }

        Map<String, Object> optionMap = new HashMap<>();
        if (uri.getQuery() != null) {
            String[] options = uri.getQuery().split("&");
            for (String option : options) {
                String[] parts = option.split("=");
                optionMap.put(parts[0], parts[1]);
            }
        }

        this.manager.dispatch(new DispatchEvent(uri, event, optionMap));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        Map<String, Object> options = new HashMap<>();

        for (OptionMapping option : event.getOptions()) {
            options.put(
                    option.getName(), switch (option.getType()) {
                        case BOOLEAN -> option.getAsBoolean();
                        case UNKNOWN, SUB_COMMAND, SUB_COMMAND_GROUP -> null;
                        case STRING -> option.getAsString();
                        case INTEGER -> option.getAsLong();
                        case CHANNEL -> option.getAsChannel();
                        case USER -> option.getAsUser();
                        case ROLE -> option.getAsRole();
                        case MENTIONABLE -> option.getAsMentionable();
                        case NUMBER -> option.getAsDouble();
                        case ATTACHMENT -> option.getAsAttachment();
                    }
            );
        }

        String        path          = InteractionTools.getInteractionPath(event);
        URI           uri           = URI.create(InteractionType.SLASH.withPrefix(path));
        DispatchEvent dispatchEvent = new DispatchEvent(uri, event, options);
        this.manager.dispatch(dispatchEvent);
    }

}
