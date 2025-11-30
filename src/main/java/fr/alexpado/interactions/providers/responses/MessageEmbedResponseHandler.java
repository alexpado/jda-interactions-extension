package fr.alexpado.interactions.providers.responses;

import fr.alexpado.interactions.interfaces.handlers.ResponseHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

public class MessageEmbedResponseHandler implements ResponseHandler<MessageEmbed> {

    @Override
    public void handle(@NotNull Request<? extends IReplyCallback> request, @NotNull MessageEmbed response) {
        IReplyCallback event = request.getEvent();
        if (event.isAcknowledged()) {
            event.getHook().sendMessageEmbeds(response).queue();
        } else {
            event.replyEmbeds(response).queue();
        }
    }
}
