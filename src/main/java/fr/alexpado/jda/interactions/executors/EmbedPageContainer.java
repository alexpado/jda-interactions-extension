package fr.alexpado.jda.interactions.executors;

import fr.alexpado.jda.interactions.InteractionTools;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.entities.responses.PaginatedResponse;
import fr.alexpado.jda.interactions.entities.responses.SimpleInteractionResponse;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.FeatureContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
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

    private final Map<Long, PaginatedResponse> responses = new HashMap<>();

    /**
     * Check if this {@link InteractionExecutor} can be used to retrieve an {@link ExecutableItem} with the given URI.
     *
     * @param uri
     *         The {@link ExecutableItem} URI.
     *
     * @return True if this {@link InteractionExecutor} can handle the request.
     */
    @Override
    public boolean canResolve(URI uri) {

        return uri.getScheme().equals("page");
    }

    /**
     * Try to match an {@link ExecutableItem} with the provided URI.
     *
     * @param path
     *         The {@link ExecutableItem} URI.
     *
     * @return An optional {@link ExecutableItem}.
     */
    @Override
    public Optional<ExecutableItem> resolve(URI path) {

        return Optional.of(this);
    }

    /**
     * Called when the {@link DispatchEvent} is ready and is about to be used on an {@link ExecutableItem}. Here you can
     * add custom options.
     *
     * @param event
     *         The {@link DispatchEvent} that will be used.
     */
    @Override
    public void prepare(DispatchEvent event) {

    }

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
     */
    @Override
    public InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<Interaction, ?>> mapping) {

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
                default -> this.handleNoAction(event);
            }
            return paginatedResponse;
        }

        this.responses.remove(id);
        return new SimpleInteractionResponse(InteractionTools.asEmbedBuilder(Color.RED, "This paginated message cannot be interacted with anymore."), true);
    }

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided {@link InteractionResponse}.
     *
     * @param response
     *         The generated {@link InteractionResponse}.
     *
     * @return True if able to handle, false otherwise.
     */
    @Override
    public boolean canHandle(InteractionResponse response) {

        return response instanceof PaginatedResponse;
    }

    /**
     * Handle the {@link InteractionResponse} resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the {@link InteractionResponse}.
     * @param executable
     *         The {@link ExecutableItem} that has been used to generate the {@link InteractionResponse}.
     * @param response
     *         The {@link InteractionResponse} to handle.
     */
    @Override
    public void handleResponse(DispatchEvent event, ExecutableItem executable, InteractionResponse response) {

        if (event.getInteraction().isAcknowledged()) {
            this.handleException(event, executable, new IllegalStateException("Unable to use pagination on an already-acknowledged interaction."));
        }

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

    /**
     * Called when an exception occurs during the execution of an {@link ExecutableItem}.
     *
     * @param event
     *         The {@link DispatchEvent} used when the error occurred.
     * @param item
     *         The {@link ExecutableItem} generating the error.
     * @param exception
     *         The {@link Exception} thrown.
     */
    @Override
    public void handleException(DispatchEvent event, ExecutableItem item, Exception exception) {

        exception.printStackTrace();
        event.getInteraction()
                .replyEmbeds(InteractionTools.asEmbed(Color.RED, "An error occurred."))
                .setEphemeral(true)
                .queue();
    }

    /**
     * Called when {@link DispatchEvent#getPath()} did not match any {@link InteractionItem}.
     *
     * @param event
     *         The unmatched {@link DispatchEvent}.
     */
    @Override
    public void handleNoAction(DispatchEvent event) {

    }

    /**
     * Called when an {@link InteractionItem} has been matched but could not be executed due to its filter ({@link
     * InteractionItem#canExecute(Interaction)}.
     *
     * @param event
     *         The {@link DispatchEvent} used.
     * @param item
     *         The {@link InteractionItem} that could not be executed.
     */
    @Override
    public void handleNonExecutable(DispatchEvent event, InteractionItem item) {

    }

}
