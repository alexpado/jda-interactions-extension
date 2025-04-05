package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

public interface InteractionContainer {

    /**
     * Add the provided object to a list of objects to scan when {@link #build(CommandListUpdateAction)} will be called.
     * <p>
     * The object must have at least one public method annotated with {@link Interact} for this call to serve a purpose.
     *
     * @param holder
     *         The object to add.
     */
    void registerInteraction(Object holder);

    /**
     * Register a new {@link ExecutableItem} with the provided {@link InteractionMeta}. This allows to register simple interaction
     * on-the-fly without bothering with annotations.
     *
     * @param meta
     *         The {@link InteractionMeta} of the new interaction.
     * @param item
     *         The {@link ExecutableItem} to use when executing the interaction.
     */
    void registerInteraction(InteractionMeta meta, ExecutableItem item);

    /**
     * Register a new {@link InteractionItem}.
     *
     * @param item
     *         The {@link InteractionItem}.
     */
    void registerInteraction(InteractionItem item);

    /**
     * Build this {@link InteractionContainer} and add all {@link InteractionItem} having their interaction type set to
     * {@link InteractionType#SLASH} as Discord Slash Commands.
     *
     * @param updateAction
     *         The {@link CommandListUpdateAction} to use to register slash commands.
     *
     * @return A {@link CommandListUpdateAction} with all commands registered. Do not forget to call
     *         {@link CommandListUpdateAction#queue()}.
     */
    CommandListUpdateAction build(CommandListUpdateAction updateAction);

    /**
     * Get all {@link InteractionItem} present in this {@link InteractionContainer}.
     *
     * @return A list of {@link InteractionItem}.
     */
    List<InteractionItem> getInteractionItems();

}
