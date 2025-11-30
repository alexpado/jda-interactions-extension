package fr.alexpado.interactions.providers.interactions.button;

import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.BaseRequest;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Adapts JDA {@link ButtonInteraction} events into {@link Request} objects.
 * <p>
 * This adapter parses URIs formatted as {@code scheme://path?param=value} or simple strings. If no scheme is present in
 * the component ID, it defaults to {@code button://}.
 */
public class ButtonSchemeAdapter implements SchemeAdapter<ButtonInteraction> {

    @Override
    public Optional<Request<ButtonInteraction>> createRequest(@NotNull ButtonInteraction event) {

        String componentId = event.getComponentId();
        URI    uri;

        try {
            if (!componentId.contains("://")) {
                uri = URI.create("button://" + componentId);
            } else {
                uri = URI.create(componentId);
            }
        } catch (IllegalArgumentException e) {
            return Optional.empty(); // Not a valid URI, ignore.
        }

        if (!"button".equals(uri.getScheme())) {
            return Optional.empty();
        }

        Request<ButtonInteraction> request = new BaseRequest<>(event, uri);
        this.parseQuery(request.getParameters(), uri.getQuery());
        SchemeAdapter.buildAttachments(request);

        return Optional.of(request);
    }

    private void parseQuery(Map<String, Object> map, String query) {

        if (query == null || query.isBlank()) return;

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                map.put(pair.substring(0, idx), pair.substring(idx + 1));
            }
        }
    }

}
