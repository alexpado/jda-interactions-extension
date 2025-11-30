package fr.alexpado.interactions.providers.interactions.button;

import fr.alexpado.interactions.annotations.Button;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.RouteResolver;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ButtonRouteResolverTests {

    @Mock
    private Request<ButtonInteraction> request;

    @Mock
    private ButtonInteraction event;

    @Test
    @DisplayName("resolve() should find registered button controller")
    void resolve_shouldFindController() {
        // Arrange
        ButtonRouteResolver resolver = new ButtonRouteResolver();
        resolver.registerController(new TestController());

        when(this.request.getUri()).thenReturn(URI.create("button://test/click"));
        when(this.request.getEvent()).thenReturn(this.event);

        // Act
        Optional<Endpoint<?>> result = resolver.resolve(this.request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(URI.create("button://test/click"), result.get().route().getUri());
    }

    @Test
    @DisplayName("resolve() should return empty for unregistered route")
    void resolve_shouldReturnEmpty_forUnknown() {

        RouteResolver resolver = new ButtonRouteResolver();

        when(this.request.getUri()).thenReturn(URI.create("button://unknown"));

        Optional<Endpoint<?>> result = resolver.resolve(this.request);
        assertTrue(result.isEmpty());
    }

    static class TestController {

        @Button(name = "test/click")
        public String onClick() {return "Clicked";}

    }

}
