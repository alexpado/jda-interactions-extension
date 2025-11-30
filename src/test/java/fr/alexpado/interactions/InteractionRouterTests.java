package fr.alexpado.interactions;


import fr.alexpado.interactions.exceptions.InteractionException;
import fr.alexpado.interactions.exceptions.UnknownInteractionException;
import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Interceptor;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.Route;
import fr.alexpado.interactions.interfaces.routing.RouteResolver;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InteractionRouterTests {

    private InteractionRouter router;

    @Mock
    private Route                                      route;
    @Mock
    private Request<SlashCommandInteractionEvent>      slashRequest;
    @Mock
    private RouteHandler<SlashCommandInteractionEvent> slashHandler;
    @Mock
    private RouteResolver                              fallbackResolver;

    private final URI testUri = URI.create("slash://test/command");

    @BeforeEach
    void setUp() {

        this.router = new InteractionRouter();
        lenient().when(this.route.getUri()).thenReturn(this.testUri);
        lenient().when(this.slashRequest.getUri()).thenReturn(this.testUri);
        lenient().when(this.slashRequest.getEvent()).thenReturn(mock(SlashCommandInteractionEvent.class));
    }

    @Test
    @DisplayName("dispatch() should find and execute the correct handler when a route exists")
    void dispatch_shouldExecuteHandler_whenRouteExists() {
        // Arrange
        when(this.slashHandler.handle(this.slashRequest)).thenReturn("Success");
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);

        // Act
        Object result = this.router.dispatch(this.slashRequest);

        // Assert
        assertEquals("Success", result);
        verify(this.slashHandler, times(1)).handle(this.slashRequest);
    }

    @Test
    @DisplayName("dispatch() should throw UnknownInteractionException when route does not exist")
    void dispatch_shouldThrowException_whenRouteDoesNotExist() {
        // Arrange
        when(this.slashRequest.getUri()).thenReturn(URI.create("slash://nonexistent"));

        // Act & Assert
        assertThrows(UnknownInteractionException.class, () -> this.router.dispatch(this.slashRequest));
        verify(this.slashHandler, never()).handle(any());
    }

    @Test
    @DisplayName("dispatch() should throw InteractionException for mismatched event types")
    void dispatch_shouldThrowException_forMismatchedTypes() {
        // Arrange
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);
        Request<ButtonInteractionEvent> buttonRequest = mock(Request.class);
        when(buttonRequest.getUri()).thenReturn(this.testUri);
        when(buttonRequest.getEvent()).thenReturn(mock(ButtonInteractionEvent.class));

        // Act & Assert
        assertThrows(InteractionException.class, () -> this.router.dispatch(buttonRequest));
    }

    @Test
    @DisplayName("dispatch() should short-circuit and return interceptor result on preHandle")
    void dispatch_shouldShortCircuit_onPreHandle() {
        // Arrange
        Interceptor interceptor = mock(Interceptor.class);
        when(interceptor.preHandle(this.route, this.slashRequest)).thenReturn(Optional.of("Intercepted"));
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);
        this.router.registerInterceptor(interceptor);

        // Act
        Object result = this.router.dispatch(this.slashRequest);

        // Assert
        assertEquals("Intercepted", result);
        verify(this.slashHandler, never()).handle(this.slashRequest);
    }

    @Test
    @DisplayName("dispatch() should modify result on postHandle")
    void dispatch_shouldModifyResult_onPostHandle() {
        // Arrange
        Interceptor interceptor = mock(Interceptor.class);
        when(this.slashHandler.handle(this.slashRequest)).thenReturn("Original");
        when(interceptor.postHandle(this.route, this.slashRequest, "Original")).thenReturn(Optional.of("Modified"));
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);
        this.router.registerInterceptor(interceptor);

        // Act
        Object result = this.router.dispatch(this.slashRequest);

        // Assert
        assertEquals("Modified", result);
        verify(this.slashHandler, times(1)).handle(this.slashRequest);
    }

    @Test
    @DisplayName("registerRoute() should normalize URI and dispatch() should find it")
    void dispatch_shouldFindRoute_withNormalizedUri() {
        // Arrange
        URI requestUriWithQuery = URI.create("slash://test/command?user=123");
        when(this.slashRequest.getUri()).thenReturn(requestUriWithQuery);
        when(this.slashHandler.handle(this.slashRequest)).thenReturn("Success");
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);

        // Act
        Object result = this.router.dispatch(this.slashRequest);

        // Assert
        assertEquals("Success", result);
    }


    @Test
    @DisplayName("resolve() should use fallback resolver if route not in map")
    void resolve_shouldUseFallbackResolver() {
        // Arrange
        fr.alexpado.interactions.structure.Endpoint<?> mockEndpoint = mock(fr.alexpado.interactions.structure.Endpoint.class);
        when(this.fallbackResolver.resolve(this.slashRequest)).thenReturn(Optional.of(mockEndpoint));

        this.router.registerResolver(this.fallbackResolver);
        // Ensure map is empty for this URI
        when(this.slashRequest.getUri()).thenReturn(URI.create("slash://unknown"));

        // Act
        Optional<fr.alexpado.interactions.structure.Endpoint<?>> result = this.router.resolve(this.slashRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(mockEndpoint, result.get());
    }

    @Test
    @DisplayName("registerRoute() should return false if route already exists")
    void registerRoute_shouldReturnFalse_onDuplicate() {
        // Arrange
        this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);

        // Act
        boolean result = this.router.registerRoute(this.route, SlashCommandInteractionEvent.class, this.slashHandler);

        // Assert
        // First one (in BeforeEach/Arrange) would be true, second one here should be false
        org.junit.jupiter.api.Assertions.assertFalse(result);
    }

}
