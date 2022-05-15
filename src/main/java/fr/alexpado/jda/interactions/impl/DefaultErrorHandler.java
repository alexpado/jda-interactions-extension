package fr.alexpado.jda.interactions.impl;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.DiscordEmbeddable;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionErrorHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * Class being used as the default {@link InteractionErrorHandler} if not overridden.
 */
public class DefaultErrorHandler implements InteractionErrorHandler {

    /**
     * Called when an exception occurs during the execution of an {@link Interaction}.
     *
     * @param event
     *         The {@link DispatchEvent} that was being executed when the exception was thrown.
     * @param exception
     *         The thrown {@link Exception}.
     */
    @Override
    public <T extends Interaction> void handleException(DispatchEvent<T> event, Exception exception) {

        if (event.getInteraction() instanceof IReplyCallback callback) {
            if (exception instanceof DiscordEmbeddable embeddable) {
                EmbedBuilder builder = embeddable.asEmbed();
                this.answer(callback, builder.build());
                return;
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("An error occurred.");
            builder.setDescription(exception.getMessage());
            builder.setFooter("You can remove this message by creating your own error handler.");
            this.answer(callback, builder.build());
        }

        // If not IReplyCallback, well, that's it.
    }

    /**
     * Called when no {@link InteractionResponseHandler} could be found for the provided object.
     *
     * @param event
     *         The {@link DispatchEvent} that was used to generate the response.
     * @param response
     *         The response object generated.
     */
    @Override
    public <T extends Interaction> void onNoResponseHandlerFound(DispatchEvent<T> event, Object response) {

        if (event.getInteraction() instanceof IReplyCallback callback) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Unable to find a response handler");
            builder.setDescription("Your interaction has been executed, but the response generated from the interaction target could not be handled.\n");
            builder.appendDescription("If you have created your own response handler, please make sure you registered it.");
            builder.setFooter("You can remove this message by creating your own error handler.");
            this.answer(callback, builder.build());
        }

        // If not IReplyCallback, well, that's it.
    }


    private <T extends Interaction & IReplyCallback> void answer(T interaction, MessageEmbed embed) {

        if (interaction.isAcknowledged()) {
            interaction.getHook().editOriginalEmbeds(embed).complete();
        } else {
            interaction.replyEmbeds(embed).setEphemeral(true).complete();
        }
    }

}
