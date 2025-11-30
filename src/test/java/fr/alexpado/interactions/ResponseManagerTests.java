package fr.alexpado.interactions;

import fr.alexpado.interactions.exceptions.ResultHandlerNotFoundException;
import fr.alexpado.interactions.interfaces.handlers.ResponseHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResponseManagerTests {


    private ResponseManager manager;

    @Mock
    private Request<SlashCommandInteractionEvent> request;
    @Mock
    private ResponseHandler<String>               stringHandler;
    @Mock
    private ResponseHandler<List>                 listHandler;
    @Mock
    private ResponseHandler<Void>                 voidHandler;

    @BeforeEach
    void setUp() {

        this.manager = new ResponseManager();
        lenient().when(this.request.getEvent()).thenReturn(mock(SlashCommandInteractionEvent.class));
    }

    @Test
    @DisplayName("processResult() should execute handler for the exact type")
    void processResult_shouldExecuteHandler_forExactType() {
        // Arrange
        this.manager.registerHandler(String.class, this.stringHandler);

        // Act
        this.manager.processResult(this.request, "Hello");

        // Assert
        verify(this.stringHandler, times(1)).handle(this.request, "Hello");
    }

    @Test
    @DisplayName("processResult() should execute handler for a compatible supertype (polymorphism)")
    void processResult_shouldExecuteHandler_forSupertype() {
        // Arrange
        this.manager.registerHandler(List.class, this.listHandler);
        List<String> arrayListResult = new ArrayList<>();

        // Act
        this.manager.processResult(this.request, arrayListResult);

        // Assert
        verify(this.listHandler, times(1)).handle(this.request, arrayListResult);
    }

    @Test
    @DisplayName("processResult() should throw exception when no handler is found")
    void processResult_shouldThrowException_whenNoHandlerFound() {
        // Arrange
        this.manager.registerHandler(String.class, this.stringHandler);

        // Act & Assert
        assertThrows(ResultHandlerNotFoundException.class, () -> this.manager.processResult(this.request, 12345));
    }

    @Test
    @DisplayName("processResult() should use Void.class handler for null results")
    void processResult_shouldHandleNull_withVoidHandler() {

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> this.manager.registerHandler(Void.class, this.voidHandler)
        );

        Assertions.assertThrows(
                NullPointerException.class,
                () -> this.manager.processResult(this.request, null)
        );
    }

    @Test
    @DisplayName("processResult() should silently ignore events that are not IReplyCallback")
    void processResult_shouldIgnoreNonReplyEvents() {
        // Arrange
        Interaction          nonReplyEvent  = mock(Interaction.class); // Not implementing IReplyCallback
        Request<Interaction> genericRequest = mock(Request.class);
        when(genericRequest.getEvent()).thenReturn(nonReplyEvent);

        // Act
        // Should not throw ResultHandlerNotFoundException even though no handler is registered for Integer
        this.manager.processResult(genericRequest, 12345);

        // Assert
        verifyNoInteractions(this.stringHandler);
    }

}
