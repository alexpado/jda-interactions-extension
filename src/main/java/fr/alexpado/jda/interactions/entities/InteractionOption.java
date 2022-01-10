package fr.alexpado.jda.interactions.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InteractionOption<T> {

    private final String                   name;
    private final String                   description;
    private final Map<String, T>           choices;
    private final Supplier<Map<String, T>> choiceSupplier;

    public InteractionOption(String name, String description) {

        this(name, description, new HashMap<>());
    }

    public InteractionOption(String name, String description, Map<String, T> choices) {

        this.name           = name;
        this.description    = description;
        this.choices        = choices;
        this.choiceSupplier = null;
    }

    public InteractionOption(String name, String description, Supplier<Map<String, T>> choiceSupplier) {

        this.name           = name;
        this.description    = description;
        this.choices        = new HashMap<>();
        this.choiceSupplier = choiceSupplier;
    }

    public Map<String, T> getChoices() {

        if (this.choiceSupplier != null) {
            return this.choiceSupplier.get();
        }

        return this.choices;
    }

}
