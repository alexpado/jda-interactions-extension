package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;

public interface InteractionItem extends ExecutableItem {

    InteractionMeta getMeta();

    String getPath();

    boolean canExecute(Interaction interaction);

}
