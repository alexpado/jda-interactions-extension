package fr.alexpado.interactions.providers.responses;

import fr.alexpado.interactions.interfaces.handlers.ResponseHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.jetbrains.annotations.NotNull;

public class StringResponseHandler implements ResponseHandler<String> {

    @Override
    public void handle(@NotNull Request<? extends IReplyCallback> request, @NotNull String response) {

        IReplyCallback event = request.getEvent();
        if (event.isAcknowledged()) {
            event.getHook().sendMessage(response).queue();
        } else {
            event.reply(response).queue();
        }
    }

}
