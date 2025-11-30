package fr.alexpado.interactions.providers.interactions.button;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ButtonSchemeAdapterTests {

    @InjectMocks
    private ButtonSchemeAdapter adapter;

    @Mock
    private ButtonInteraction event;

    @Test
    @DisplayName("createRequest() should handle plain IDs by prepending scheme")
    void createRequest_shouldHandlePlainId() {

        when(this.event.getComponentId()).thenReturn("my/action");

        Optional<Request<ButtonInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isPresent());
        assertEquals(URI.create("button://my/action"), result.get().getUri());
    }

    @Test
    @DisplayName("createRequest() should handle IDs with scheme")
    void createRequest_shouldHandleSchemeId() {

        when(this.event.getComponentId()).thenReturn("button://my/action");

        Optional<Request<ButtonInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isPresent());
        assertEquals(URI.create("button://my/action"), result.get().getUri());
    }

    @Test
    @DisplayName("createRequest() should parse query parameters")
    void createRequest_shouldParseQuery() {

        when(this.event.getComponentId()).thenReturn("my/action?id=123&mode=edit");

        Optional<Request<ButtonInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isPresent());
        Request<ButtonInteraction> request = result.get();
        assertEquals("123", request.getParameters().get("id"));
        assertEquals("edit", request.getParameters().get("mode"));
    }

    @Test
    @DisplayName("createRequest() should ignore non-button schemes")
    void createRequest_shouldIgnoreInvalidScheme() {

        when(this.event.getComponentId()).thenReturn("https://google.com");

        Optional<Request<ButtonInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createRequest() should ignore malformed URIs")
    void createRequest_shouldIgnoreMalformedUri() {

        when(this.event.getComponentId()).thenReturn("button://  invalid  ");

        Optional<Request<ButtonInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isEmpty());
    }

}
