package fr.alexpado.jda.interactions.entities;

import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DispatchEvent {

    private final URI                 path;
    private final JdaInteraction      interaction;
    private final Map<String, Object> options;

    public DispatchEvent(URI path, JdaInteraction interaction) {

        this(path, interaction, new HashMap<>());
    }

    public DispatchEvent(URI path, JdaInteraction interaction, Map<String, Object> options) {

        this.path        = path;
        this.interaction = interaction;
        this.options     = options;
    }

    public URI getPath() {

        return path;
    }

    public JdaInteraction getInteraction() {

        return interaction;
    }

    public Map<String, Object> getOptions() {

        return options;
    }

}
