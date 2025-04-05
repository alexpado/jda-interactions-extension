package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;

public interface InteractionItem extends ExecutableItem {

    /**
     * Retrieves this {@link InteractionItem}'s {@link InteractionMeta}.
     *
     * @return The {@link InteractionItem}'s {@link InteractionMeta}.
     */
    InteractionMeta getMeta();

    /**
     * Retrieves this {@link InteractionItem}'s URI as string.
     *
     * @return The {@link InteractionItem}'s URI.
     */
    String getPath();

    /**
     * Check if this {@link InteractionItem} can be used with the given {@link Interaction}. This is useful if you want to
     * restrict some actions to some guilds.
     *
     * @param interaction
     *         The Discord {@link Interaction}.
     *
     * @return True if executable, false otherwise.
     */
    boolean canExecute(Interaction interaction);

}
