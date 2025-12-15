package fr.alexpado.jda.interactions.impl.handlers;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Class implementing the {@link InteractionResponseHandler} where its sole purpose is to handle null-responses.
 * <p>
 * Although discouraged, null-responses are valid but will provide no feedback to the user.
 * <p>
 * This may even be more problematic when the response is from an {@link SlashCommandInteraction} where the user expect
 * an answer explicitly, just as Discord, so a default response will be sent instead.
 */
@Deprecated
public class SinkResponseHandler implements InteractionResponseHandler {

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided response.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The object representing the response given by an interaction.
     *
     * @return True if able to handle, false otherwise.
     */
    @Override
    public <T extends Interaction> boolean canHandle(DispatchEvent<T> event, @Nullable Object response) {

        return response == null;
    }

    /**
     * Handle the response resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The {@link Object} to handle.
     */
    @Override
    public <T extends Interaction> void handleResponse(DispatchEvent<T> event, @Nullable Object response) {

        if (event.interaction() instanceof SlashCommandInteraction slash) {
            this.answer(slash, data -> data.setContent("*Nothing to display*"));
        } else if (event.interaction() instanceof ButtonInteraction button) {
            this.acknowledgeButton(button);
        } else if (event.interaction() instanceof CommandAutoCompleteInteraction auto) {
            this.acknowledgeAutocomplete(auto);
        }
    }

    private <T extends Interaction & IReplyCallback> void answer(T interaction, Consumer<MessageRequest<?>> consumer) {

        if (interaction.isAcknowledged()) {
            MessageEditBuilder builder = new MessageEditBuilder();
            consumer.accept(builder);
            interaction.getHook().editOriginal(builder.build()).complete();
        } else {
            MessageCreateBuilder builder = new MessageCreateBuilder();
            consumer.accept(builder);
            interaction.reply(builder.build()).setEphemeral(true).complete();
        }
    }

    private void acknowledgeButton(IReplyCallback button) {

        if (!button.isAcknowledged()) {
            button.deferReply().complete();
        }
    }

    private void acknowledgeAutocomplete(IAutoCompleteCallback auto) {

        if (!auto.isAcknowledged()) {
            auto.replyChoices().complete();
        }
    }

}
