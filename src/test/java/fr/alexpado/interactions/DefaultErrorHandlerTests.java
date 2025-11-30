package fr.alexpado.interactions;

import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.internal.DefaultErrorHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultErrorHandlerTests {

    @InjectMocks
    private DefaultErrorHandler errorHandler;

    @Mock
    private IReplyCallback callbackEvent;

    @Mock
    private InteractionHook hook;

    @Mock
    private Request<?> request;

    @Test
    @DisplayName("handle() should use replyEmbeds() when interaction is not acknowledged")
    void handle_shouldReply_whenNotAcknowledged() {
        // Arrange
        when(this.callbackEvent.isAcknowledged()).thenReturn(false);
        ReplyCallbackAction action = mock(ReplyCallbackAction.class);
        when(this.callbackEvent.replyEmbeds(any(MessageEmbed.class))).thenReturn(action);
        when(action.setEphemeral(true)).thenReturn(action);

        // Act
        this.errorHandler.handle(new RuntimeException(), this.callbackEvent, this.request);

        // Assert
        verify(this.callbackEvent).replyEmbeds(any(MessageEmbed.class));
        verify(action).setEphemeral(true);
        verify(action).queue();
    }

    @Test
    @DisplayName("handle() should use hook.sendMessageEmbeds() when interaction is acknowledged")
    void handle_shouldUseHook_whenAcknowledged() {
        // Arrange
        when(this.callbackEvent.isAcknowledged()).thenReturn(true);
        when(this.callbackEvent.getHook()).thenReturn(this.hook);

        WebhookMessageCreateAction<Message> action = mock(WebhookMessageCreateAction.class);
        when(this.hook.sendMessageEmbeds(any(MessageEmbed.class))).thenReturn(action);
        when(action.setEphemeral(true)).thenReturn(action);

        // Act
        this.errorHandler.handle(new RuntimeException(), this.callbackEvent, this.request);

        // Assert
        verify(this.hook).sendMessageEmbeds(any(MessageEmbed.class));
        verify(action).setEphemeral(true);
        verify(action).queue();
    }

}
