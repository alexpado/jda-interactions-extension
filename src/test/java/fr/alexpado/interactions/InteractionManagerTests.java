package fr.alexpado.interactions;

import fr.alexpado.interactions.exceptions.UnknownInteractionException;
import fr.alexpado.interactions.exceptions.UnsupportedInteractionException;
import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.handlers.ErrorHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionManagerTests {

    @InjectMocks
    private InteractionManager manager;

    @Mock
    private InteractionRouter                           router;
    @Mock
    private ResponseManager                             responseManager;
    @Mock
    private ErrorHandler                                errorHandler;
    @Mock
    private SchemeAdapter<SlashCommandInteractionEvent> slashAdapter;
    @Mock
    private Request<SlashCommandInteractionEvent>       slashRequest;
    @Mock
    private SlashCommandInteractionEvent                slashEvent;

    @Test
    @DisplayName("processEvent() should execute the full lifecycle successfully")
    void processEvent_shouldExecuteFullLifecycle() {
        // Arrange
        this.manager = new InteractionManager(this.router, this.responseManager);
        when(this.slashAdapter.createRequest(this.slashEvent)).thenReturn(Optional.of(this.slashRequest));
        when(this.router.dispatch(this.slashRequest)).thenReturn("Success");
        this.manager.registerAdapter(SlashCommandInteractionEvent.class, this.slashAdapter);

        // Act
        this.manager.processEvent(this.slashEvent);

        // Assert
        verify(this.router, times(1)).dispatch(this.slashRequest);
        verify(this.responseManager, times(1)).processResult(this.slashRequest, "Success");
        verify(this.errorHandler, never()).handle(any(), any(), any());
    }

    @Test
    @DisplayName("processEvent() should call ErrorHandler when router throws an exception")
    void processEvent_shouldCallErrorHandler_whenRouterThrows() {
        // Arrange
        this.manager = new InteractionManager(this.router, this.responseManager);
        UnknownInteractionException exception = new UnknownInteractionException("Not found", this.slashRequest);
        when(this.slashAdapter.createRequest(this.slashEvent)).thenReturn(Optional.of(this.slashRequest));
        when(this.router.dispatch(this.slashRequest)).thenThrow(exception);
        this.manager.setErrorHandler(this.errorHandler);
        this.manager.registerAdapter(SlashCommandInteractionEvent.class, this.slashAdapter);

        // Act
        this.manager.processEvent(this.slashEvent);

        // Assert
        verify(this.responseManager, never()).processResult(any(), any());
        verify(this.errorHandler, times(1)).handle(eq(exception), eq(this.slashEvent), eq(this.slashRequest));
    }

    @Test
    @DisplayName("processEvent() should call ErrorHandler with UnsupportedInteractionException when no adapter is registered")
    void processEvent_shouldCallErrorHandler_whenNoAdapterIsRegistered() {
        // Arrange
        this.manager = new InteractionManager(this.router, this.responseManager);
        this.manager.setErrorHandler(this.errorHandler);

        // Act
        this.manager.processEvent(this.slashEvent);

        // Assert
        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(this.errorHandler, times(1)).handle(throwableCaptor.capture(), eq(this.slashEvent), eq(null));

        Throwable captured = throwableCaptor.getValue();
        assertInstanceOf(UnsupportedInteractionException.class, captured);
        assertEquals(
                "No adapter found for event type net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent",
                captured.getMessage()
        );
    }

    @Test
    @DisplayName("processEvent() should do nothing when no adapter can create a request")
    void processEvent_shouldDoNothing_whenNoAdapterMatches() {
        // Arrange
        this.manager = new InteractionManager(this.router, this.responseManager);
        when(this.slashAdapter.createRequest(this.slashEvent)).thenReturn(Optional.empty());
        this.manager.registerAdapter(SlashCommandInteractionEvent.class, this.slashAdapter);

        // Act
        this.manager.processEvent(this.slashEvent);

        // Assert
        verify(this.router, never()).dispatch(any());
        verify(this.responseManager, never()).processResult(any(), any());
        verify(this.errorHandler, never()).handle(any(), any(), any());
    }

    @Test
    @DisplayName("processEvent() should try next adapter if first returns empty")
    void processEvent_shouldChainAdapters() {
        // Arrange
        SchemeAdapter<SlashCommandInteractionEvent> wrongAdapter = mock(SchemeAdapter.class);

        when(wrongAdapter.createRequest(this.slashEvent)).thenReturn(Optional.empty());
        when(this.slashAdapter.createRequest(this.slashEvent)).thenReturn(Optional.of(this.slashRequest));
        when(this.router.dispatch(this.slashRequest)).thenReturn("Success");

        // Register both
        this.manager.registerAdapter(SlashCommandInteractionEvent.class, wrongAdapter);
        this.manager.registerAdapter(SlashCommandInteractionEvent.class, this.slashAdapter);

        // Act
        this.manager.processEvent(this.slashEvent);

        // Assert
        verify(wrongAdapter).createRequest(this.slashEvent);
        verify(this.slashAdapter).createRequest(this.slashEvent); // Should be called after first failed
        verify(this.router).dispatch(this.slashRequest);
    }

}
