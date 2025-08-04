package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.meta.InteractionMeta;

/**
 * Interface representing an object capable of holding {@link InteractionMeta}.
 */
public interface MetaContainer {

    /**
     * Retrieve the {@link InteractionMeta} of this {@link MetaContainer}.
     *
     * @return The {@link InteractionMeta} instance.
     */
    InteractionMeta getMeta();

}
