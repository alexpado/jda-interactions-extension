package fr.alexpado.jda.interactions.executors;

import fr.alexpado.jda.interactions.InteractionTools;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.entities.responses.PaginatedResponse;
import fr.alexpado.jda.interactions.entities.responses.SimpleInteractionResponse;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.FeatureContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionItem;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EmbedPageContainer implements FeatureContainer {

    private Map<Long, PaginatedResponse> responses = new HashMap<>();

    @Override
    public boolean canResolve(URI uri) {

        return uri.getScheme().equals("page");
    }

    @Override
    public Optional<ExecutableItem> resolve(URI path) {

        return Optional.of(this);
    }

    @Override
    public void prepare(DispatchEvent event) {

    }

    @Override
    public InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<Interaction, ?>> mapping) {

        System.out.println("Embed: Handling page URI " + event.getPath());
        System.out.println("-- Registered paginated embed:");

        for (Long ids : this.responses.keySet()) {
            System.out.println("   " + ids);
        }
        System.out.println("--");

        long   id     = Long.parseLong(event.getPath().getHost());
        String action = event.getPath().getPath();

        if (!this.responses.containsKey(id)) {
            return new SimpleInteractionResponse(InteractionTools.asEmbedBuilder(Color.RED, "This paginated message cannot be interacted with anymore."), true);
        }

        PaginatedResponse paginatedResponse = this.responses.get(id);
        LocalDateTime     timeLimit         = paginatedResponse.getTime().plusMinutes(5);

        if (timeLimit.isAfter(LocalDateTime.now())) {
            switch (action) {
                case "/next" -> paginatedResponse.nextPage();
                case "/previous" -> paginatedResponse.previousPage();
                default -> System.out.println("Embed: Action not recognized (" + action + ")");
            }
            return paginatedResponse;
        }

        this.responses.remove(id);
        return new SimpleInteractionResponse(InteractionTools.asEmbedBuilder(Color.RED, "This paginated message cannot be interacted with anymore."), true);
    }

    @Override
    public boolean canHandle(InteractionResponse response) {

        return response instanceof PaginatedResponse;
    }

    @Override
    public void handleResponse(DispatchEvent event, InteractionResponse response) {
        // As #canHandle has been called beforehand, this is safe.
        PaginatedResponse paginatedResponse = (PaginatedResponse) response;

        Message        message;
        MessageBuilder builder = new MessageBuilder();
        MessageEmbed   embed   = paginatedResponse.getEmbed().build();
        builder.setEmbeds(embed);

        if (event.getInteraction() instanceof SlashCommandEvent slash) {
            InteractionHook hook = slash.reply(builder.build()).complete();
            message = hook.retrieveOriginal().complete();
            this.responses.put(message.getIdLong(), paginatedResponse);
        } else if (event.getInteraction() instanceof ButtonClickEvent button) {
            message = button.deferEdit().complete().retrieveOriginal().complete();
        } else {
            this.handleNoAction(event);
            return;
        }

        builder.setEmbeds(embed);
        builder.setActionRows(
                ActionRow.of(
                        paginatedResponse.getPreviousButton(message.getIdLong()),
                        paginatedResponse.getNextButton(message.getIdLong())
                )
        );

        message.editMessage(builder.build()).queue();
    }

    @Override
    public void handleException(DispatchEvent event, ExecutableItem item, Exception exception) {

        exception.printStackTrace();

        event.getInteraction().replyEmbeds(InteractionTools.asEmbed(Color.RED, "An error occurred.")).setEphemeral(true)
                .queue();
    }

    @Override
    public void handleNoAction(DispatchEvent event) {

        System.out.println("Embed: Could not execute");
    }

    @Override
    public void handleNonExecutable(DispatchEvent event, InteractionItem item) {
        // NO-OP
    }

}
