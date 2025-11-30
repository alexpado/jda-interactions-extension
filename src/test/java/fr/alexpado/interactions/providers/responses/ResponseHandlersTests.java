package fr.alexpado.interactions.providers.responses;

import fr.alexpado.interactions.interfaces.handlers.ResponseHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ResponseHandlersTests {

    @Test
    @DisplayName("StringResponseHandler should use reply() if not acknowledged")
    void stringHandler_shouldReply() {

        ResponseHandler<String> handler = new StringResponseHandler();
        Request<IReplyCallback> req     = mock(Request.class);
        IReplyCallback          event   = mock(IReplyCallback.class);

        when(req.getEvent()).thenReturn(event);
        when(event.isAcknowledged()).thenReturn(false);
        when(event.reply(anyString())).thenReturn(mock(ReplyCallbackAction.class));

        handler.handle(req, "test");
        verify(event).reply("test");
    }

    @Test
    @DisplayName("StringResponseHandler should use hook() if acknowledged")
    void stringHandler_shouldUseHook() {

        ResponseHandler<String> handler = new StringResponseHandler();
        Request<IReplyCallback> req     = mock(Request.class);
        IReplyCallback          event   = mock(IReplyCallback.class);
        InteractionHook         hook    = mock(InteractionHook.class);

        when(req.getEvent()).thenReturn(event);
        when(event.isAcknowledged()).thenReturn(true);
        when(event.getHook()).thenReturn(hook);
        when(hook.sendMessage(anyString())).thenReturn(mock(WebhookMessageCreateAction.class));

        handler.handle(req, "test");
        verify(hook).sendMessage("test");
    }

    @Test
    @DisplayName("MessageEmbedResponseHandler should use replyEmbeds()")
    void embedHandler_shouldReply() {

        ResponseHandler<MessageEmbed> handler = new MessageEmbedResponseHandler();
        Request<IReplyCallback>       req     = mock(Request.class);
        IReplyCallback                event   = mock(IReplyCallback.class);

        when(req.getEvent()).thenReturn(event);
        when(event.isAcknowledged()).thenReturn(false);
        when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(mock(ReplyCallbackAction.class));

        handler.handle(req, mock(MessageEmbed.class));
        verify(event).replyEmbeds(any(MessageEmbed.class));
    }

}
