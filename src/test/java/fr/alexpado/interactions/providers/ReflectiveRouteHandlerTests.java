package fr.alexpado.interactions.providers;

import fr.alexpado.interactions.annotations.Attribute;
import fr.alexpado.interactions.exceptions.InteractionException;
import fr.alexpado.interactions.interfaces.handlers.RouteHandler;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.jda.interactions.annotations.Param;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReflectiveRouteHandlerTests {

    // --- Inner class to act as the controller ---
    static class TestController {

        public String noArgs() {

            return "ok";
        }

        public String withParam(@Param("id") long id, @Param("name") String name) {

            return id + ":" + name;
        }

        public String withAttribute(@Attribute("user_id") String userId) {

            return "User:" + userId;
        }

        public String withContext(Request<?> req, Interaction interaction) {

            return (req != null) + ":" + (interaction != null);
        }

        public String withAttachment(User user) {

            return user.getName();
        }

        public String withConversion(@Param("active") boolean active, @Param("type") TestEnum type) {

            return active + "-" + type;
        }

        public String withPrimitives(@Param("num") int num, @Param("dbl") double dbl) {

            return num + "/" + dbl;
        }

        public void voidMethod() {
            // Invalid
        }

        public String nullReturning() {

            return null;
        }

    }

    enum TestEnum {A, B}

    // --- Tests ---

    @Test
    @DisplayName("Constructor should throw if method returns void")
    void constructor_shouldThrow_onVoidMethod() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method         method     = TestController.class.getMethod("voidMethod");

        assertThrows(
                IllegalArgumentException.class, () ->
                        new ReflectiveRouteHandler<>(controller, method)
        );
    }

    @Test
    @DisplayName("handle() should invoke simple method")
    void handle_shouldInvokeSimpleMethod() throws NoSuchMethodException {

        TestController            controller = new TestController();
        Method                    method     = TestController.class.getMethod("noArgs");
        RouteHandler<Interaction> handler    = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);

        assertEquals("ok", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should resolve @Param and convert types")
    void handle_shouldResolveParams() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method method = TestController.class.getMethod(
                "withParam",
                long.class,
                String.class
        );
        RouteHandler<Interaction> handler = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Map<String, Object>  params  = new HashMap<>();
        params.put("id", "12345"); // Passed as String usually
        params.put("name", "Alex");
        when(request.getParameters()).thenReturn(params);

        assertEquals("12345:Alex", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should resolve primitives conversion")
    void handle_shouldResolvePrimitives() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method method = TestController.class.getMethod(
                "withPrimitives",
                int.class,
                double.class
        );
        RouteHandler<Interaction> handler = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Map<String, Object>  params  = new HashMap<>();
        params.put("num", "42");
        params.put("dbl", "3.14");
        when(request.getParameters()).thenReturn(params);

        assertEquals("42/3.14", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should resolve @Attribute")
    void handle_shouldResolveAttributes() throws NoSuchMethodException {

        TestController            controller = new TestController();
        Method                    method     = TestController.class.getMethod("withAttribute", String.class);
        RouteHandler<Interaction> handler    = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Map<String, Object>  attrs   = new HashMap<>();
        attrs.put("user_id", "uid-99");
        when(request.getAttributes()).thenReturn(attrs);

        assertEquals("User:uid-99", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should inject Context (Request and Interaction)")
    void handle_shouldInjectContext() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method method = TestController.class.getMethod(
                "withContext",
                Request.class,
                Interaction.class
        );
        RouteHandler<Interaction> handler = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Interaction          event   = mock(Interaction.class);
        when(request.getEvent()).thenReturn(event);

        assertEquals("true:true", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should resolve Attachment for unknown types")
    void handle_shouldResolveAttachment() throws NoSuchMethodException {

        TestController            controller = new TestController();
        Method                    method     = TestController.class.getMethod("withAttachment", User.class);
        RouteHandler<Interaction> handler    = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        User                 user    = mock(User.class);
        when(user.getName()).thenReturn("DiscordUser");

        // Mocking the generic method getAttachment
        when(request.getAttachment(User.class)).thenReturn(user);

        assertEquals("DiscordUser", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should convert Enums correctly")
    void handle_shouldConvertEnums() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method method = TestController.class.getMethod(
                "withConversion",
                boolean.class,
                TestEnum.class
        );
        RouteHandler<Interaction> handler = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Map<String, Object>  params  = new HashMap<>();
        params.put("active", "true");
        params.put("type", "B");
        when(request.getParameters()).thenReturn(params);

        assertEquals("true-B", handler.handle(request));
    }

    @Test
    @DisplayName("handle() should throw InteractionException on invalid Enum")
    void handle_shouldThrowOnInvalidEnum() throws NoSuchMethodException {

        TestController controller = new TestController();
        Method method = TestController.class.getMethod(
                "withConversion",
                boolean.class,
                TestEnum.class
        );
        ReflectiveRouteHandler<Interaction> handler = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);
        Map<String, Object>  params  = new HashMap<>();
        params.put("active", "true");
        params.put("type", "INVALID_VAL");
        when(request.getParameters()).thenReturn(params);

        assertThrows(InteractionException.class, () -> handler.handle(request));
    }

    @Test
    @DisplayName("handle() should throw InteractionException if handler returns null")
    void handle_shouldThrowIfResultNull() throws NoSuchMethodException {

        TestController                      controller = new TestController();
        Method                              method     = TestController.class.getMethod("nullReturning");
        ReflectiveRouteHandler<Interaction> handler    = new ReflectiveRouteHandler<>(controller, method);

        Request<Interaction> request = mock(Request.class);

        assertThrows(InteractionException.class, () -> handler.handle(request));
    }

}
