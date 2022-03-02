package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.bridge.JdaInteraction;
import fr.alexpado.jda.interactions.meta.InteractionMeta;

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
     * Check if this {@link InteractionItem} can be used with the given {@link JdaInteraction}. This is useful if you
     * want to restrict some actions to some guilds.
     *
     * @param interaction
     *         The Discord {@link JdaInteraction}.
     *
     * @return True if executable, false otherwise.
     */
    boolean canExecute(JdaInteraction interaction);

}
