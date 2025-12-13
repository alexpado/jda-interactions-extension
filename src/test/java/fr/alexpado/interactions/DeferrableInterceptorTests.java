package fr.alexpado.interactions;

import fr.alexpado.interactions.interfaces.routing.DeferrableRoute;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.Route;
import fr.alexpado.interactions.internal.DeferrableInterceptor;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeferrableInterceptorTests {

    @InjectMocks
    private DeferrableInterceptor interceptor;

    @Mock
    private DeferrableRoute deferrableRoute;

    @Mock
    private Route normalRoute;

    @Mock
    private Request<IReplyCallback> request;

    @Mock
    private IReplyCallback event;

    @Test
    @DisplayName("preHandle() should do nothing if route is not DeferrableRoute")
    void preHandle_shouldIgnoreNormalRoute() {

        Endpoint<Interaction> endpoint = new Endpoint<>(this.normalRoute, null, Interaction.class);

        Optional<Object> result = this.interceptor.preHandle(endpoint, this.request);

        assertTrue(result.isEmpty());
        verifyNoInteractions(this.request);
    }

    @Test
    @DisplayName("preHandle() should defer if route is configured to defer")
    void preHandle_shouldDefer_whenConfigured() {
        // Arrange
        when(this.request.getEvent()).thenReturn(this.event);
        when(this.deferrableRoute.isDeferred()).thenReturn(true);
        when(this.deferrableRoute.isEphemeral()).thenReturn(true);

        ReplyCallbackAction action = mock(ReplyCallbackAction.class);
        InteractionHook     hook   = mock(InteractionHook.class);

        when(this.event.deferReply(true)).thenReturn(action);
        when(action.complete()).thenReturn(hook);

        Endpoint<Interaction> endpoint = new Endpoint<>(this.deferrableRoute, null, Interaction.class);

        // Act
        Optional<Object> result = this.interceptor.preHandle(endpoint, this.request);

        // Assert
        assertTrue(result.isEmpty()); // Should not short-circuit
        verify(this.event).deferReply(true);
        verify(this.request).addAttachment(InteractionHook.class, hook);
    }

    @Test
    @DisplayName("preHandle() should not defer if route returns false for isDeferred")
    void preHandle_shouldNotDefer_whenDisabled() {
        // Arrange
        when(this.request.getEvent()).thenReturn(this.event);
        when(this.deferrableRoute.isDeferred()).thenReturn(false);

        Endpoint<Interaction> endpoint = new Endpoint<>(this.deferrableRoute, null, Interaction.class);

        // Act
        this.interceptor.preHandle(endpoint, this.request);

        // Assert
        verify(this.event, never()).deferReply(anyBoolean());
    }

}
