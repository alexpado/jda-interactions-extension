package fr.alexpado.interactions.internal;

import fr.alexpado.interactions.interfaces.handlers.ErrorHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;


/**
 * The default {@link ErrorHandler} implementation.
 * <p>
 * This handler logs the full exception to the console and sends a simple, ephemeral "An error occurred" message to the
 * user.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultErrorHandler.class);

    @Override
    public void handle(@NotNull Throwable throwable, @NotNull Interaction interaction, Request<?> request) {

        LOGGER.error("An exception occurred while processing an interaction.", throwable);

        if (request != null) {
            LOGGER.error("Interaction context: URI={}, Body={}", request.getUri(), request.getParameters());
        }

        if (!(interaction instanceof IReplyCallback callback)) {
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("An Error Occurred");
        builder.setDescription("Something went wrong while processing your request. Please try again later.");
        builder.setColor(Color.RED);

        if (callback.isAcknowledged()) {
            callback.getHook().sendMessageEmbeds(builder.build()).setEphemeral(true).queue();
        } else {
            callback.replyEmbeds(builder.build()).setEphemeral(true).queue();
        }
    }

}
