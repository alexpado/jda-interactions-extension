package fr.alexpado.jda.interactions.meta;

import fr.alexpado.jda.interactions.annotations.Choice;

public class ChoiceMeta {

    private final String id;
    private final String display;

    public ChoiceMeta(String id, String display) {

        this.id      = id;
        this.display = display;
    }

    public ChoiceMeta(Choice choice) {

        this.id      = choice.id();
        this.display = choice.display();
    }

    public String getId() {

        return this.id;
    }

    public String getDisplay() {

        return this.display;
    }

}
