package fr.alexpado.jda.interactions.impl.handlers;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.Nullable;

/**
 * Class implementing the {@link InteractionResponseHandler} where its sole purpose is to handle null-responses.
 * <p>
 * Although discouraged, null-responses are valid but will provide no feedback to the user.
 * <p>
 * This may even be more problematic when the response is from an {@link SlashCommandInteraction} where the user expect
 * an answer explicitly, just as Discord, so a default response will be sent instead.
 */
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

        //noinspection ChainOfInstanceofChecks
        if (event.getInteraction() instanceof SlashCommandInteraction slash) {
            MessageBuilder builder = new MessageBuilder();
            builder.setContent("*Nothing to display*");
            this.answer(slash, builder.build());
        } else if (event.getInteraction() instanceof ButtonInteraction button) {
            if (!button.isAcknowledged()) {
                button.deferReply().complete();
            }
        } else if (event.getInteraction() instanceof CommandAutoCompleteInteraction auto) {
            auto.replyChoices().complete();
        }

    }


    private <T extends Interaction & IReplyCallback> void answer(T interaction, Message embed) {

        if (interaction.isAcknowledged()) {
            interaction.getHook().editOriginal(embed).complete();
        } else {
            interaction.reply(embed).setEphemeral(true).complete();
        }
    }
}
