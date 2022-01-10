package fr.alexpado.jda.interactions.entities;

import net.dv8tion.jda.api.interactions.Interaction;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class DispatchEvent {

    private final URI                 path;
    private final Interaction         interaction;
    private final Map<String, Object> options;

    public DispatchEvent(URI path, Interaction interaction) {

        this(path, interaction, new HashMap<>());
    }

    public DispatchEvent(URI path, Interaction interaction, Map<String, Object> options) {

        this.path        = path;
        this.interaction = interaction;
        this.options     = options;
    }

    public URI getPath() {

        return path;
    }

    public Interaction getInteraction() {

        return interaction;
    }

    public Map<String, Object> getOptions() {

        return options;
    }
}
