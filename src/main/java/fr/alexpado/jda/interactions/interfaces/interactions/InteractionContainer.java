package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.List;

public interface InteractionContainer {

    <T> void registerInteraction(T holder);

    void registerInteraction(InteractionMeta meta, ExecutableItem item);

    void registerInteraction(InteractionItem item);

    CommandListUpdateAction build(CommandListUpdateAction updateAction);

    List<InteractionItem> getInteractionItems();

}
