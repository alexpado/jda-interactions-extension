package fr.alexpado.interactions.providers.interactions.button;

import fr.alexpado.interactions.annotations.Button;
import fr.alexpado.interactions.interfaces.routing.DeferrableRoute;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.RouteResolver;
import fr.alexpado.interactions.providers.ReflectiveRouteHandler;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A self-contained {@link RouteResolver} that manages Button interactions using annotations.
 */
public class ButtonRouteResolver implements RouteResolver {

    private final Map<URI, Endpoint<ButtonInteraction>> endpoints = new HashMap<>();

    /**
     * Registers a controller object. Scans for {@link Button} annotations.
     *
     * @param controller
     *         The object to scan.
     */
    public void registerController(Object controller) {

        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Button.class)) {
                Button annotation = method.getAnnotation(Button.class);
                URI    uri        = URI.create("button://" + annotation.name());

                Endpoint<ButtonInteraction> endpoint = new Endpoint<>(
                        DeferrableRoute.of(uri, method),
                        new ReflectiveRouteHandler<>(controller, method),
                        ButtonInteraction.class
                );

                this.endpoints.put(uri, endpoint);
            }
        }
    }

    @Override
    public <T extends Interaction> Optional<Endpoint<?>> resolve(Request<T> request) {

        URI uri = Request.normalize(request.getUri());

        if ("button".equals(uri.getScheme()) && request.getEvent() instanceof ButtonInteraction) {
            return Optional.ofNullable(this.endpoints.get(uri));
        }

        return Optional.empty();
    }

}
