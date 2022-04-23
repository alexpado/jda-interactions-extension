package fr.alexpado.jda.interactions.meta;

import fr.alexpado.jda.interactions.annotations.Choice;

/**
 * Class version of the annotation {@link Choice}.
 */
public class ChoiceMeta {

    private final String id;
    private final String display;

    /**
     * Create a new {@link ChoiceMeta}.
     *
     * @param id
     *         The choice id.
     * @param display
     *         The choice display text.
     */
    public ChoiceMeta(String id, String display) {

        this.id      = id;
        this.display = display;
    }

    /**
     * Create a new {@link ChoiceMeta}.
     *
     * @param choice
     *         The {@link Choice} annotation from which data will be loaded.
     */
    public ChoiceMeta(Choice choice) {

        this.id      = choice.id();
        this.display = choice.display();
    }

    /**
     * Retrieve this {@link ChoiceMeta} id.
     *
     * @return The id
     */
    public String getId() {

        return this.id;
    }

    /**
     * Retrieve this {@link ChoiceMeta} display text.
     *
     * @return The display text
     */
    public String getDisplay() {

        return this.display;
    }

    /**
     * Check if the id or the display text contains the provided string (case-insensitive).
     *
     * @param other
     *         The string to check against
     *
     * @return True if the id or the display text contains the provided string, false otherwise.
     */
    public boolean contains(String other) {

        return this.id.toLowerCase().contains(other.toLowerCase()) ||
               this.display.toLowerCase().contains(other.toLowerCase());
    }

}
