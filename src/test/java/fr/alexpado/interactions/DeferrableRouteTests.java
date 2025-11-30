package fr.alexpado.interactions;

import fr.alexpado.interactions.annotations.Deferrable;
import fr.alexpado.interactions.interfaces.routing.DeferrableRoute;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DeferrableRouteTests {

    @Test
    @DisplayName("of() should create route with default values when no annotation present")
    void of_shouldHaveDefaults_whenNoAnnotation() throws NoSuchMethodException {

        Method method = Controller.class.getMethod("standard");
        URI    uri    = URI.create("app://test");

        DeferrableRoute route = DeferrableRoute.of(uri, method);

        assertEquals(uri, route.getUri());
        assertFalse(route.isDeferred());
        assertFalse(route.isEphemeral());
    }

    @Test
    @DisplayName("of() should reflect annotation values")
    void of_shouldReflectAnnotation() throws NoSuchMethodException {

        Method method = Controller.class.getMethod("deferred");
        URI    uri    = URI.create("app://test");

        DeferrableRoute route = DeferrableRoute.of(uri, method);

        assertTrue(route.isDeferred());
        assertTrue(route.isEphemeral());
    }

    // Dummy class for reflection
    private static class Controller {

        public void standard() {}

        @Deferrable(ephemeral = true)
        public void deferred() {}

    }

}
