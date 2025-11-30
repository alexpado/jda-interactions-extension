package fr.alexpado.interactions.providers;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class BaseRequestTests {

    @Test
    @DisplayName("Constructor should initialize maps and fields")
    void constructor_shouldInitialize() {

        Interaction event = mock(Interaction.class);
        URI         uri   = URI.create("app://test");

        Request<Interaction> request = new BaseRequest<>(event, uri);

        assertEquals(event, request.getEvent());
        assertEquals(uri, request.getUri());
        assertNotNull(request.getParameters());
        assertNotNull(request.getAttributes());
    }

    @Test
    @DisplayName("addAttachment/getAttachment should handle type safety")
    void attachment_shouldHandleTypeSafety() {

        Interaction              event   = mock(Interaction.class);
        BaseRequest<Interaction> request = new BaseRequest<>(event, URI.create("app://test"));
        User                     user    = mock(User.class);

        // Add correct type
        request.addAttachment(User.class, user);

        // Retrieve correct type
        User retrieved = request.getAttachment(User.class);
        assertEquals(user, retrieved);
    }

    @Test
    @DisplayName("getAttachment should return null if not present")
    void getAttachment_shouldReturnNull_whenMissing() {

        Request<Interaction> request = new BaseRequest<>(mock(Interaction.class), URI.create("app://test"));
        assertNull(request.getAttachment(User.class));
    }

    @Test
    @DisplayName("Parameters and Attributes maps should be mutable")
    void maps_shouldBeMutable() {

        Request<Interaction> request = new BaseRequest<>(mock(Interaction.class), URI.create("app://test"));

        request.getParameters().put("key", "value");
        request.getAttributes().put("attr", 123);

        assertEquals("value", request.getParameters().get("key"));
        assertEquals(123, request.getAttributes().get("attr"));
    }

}
