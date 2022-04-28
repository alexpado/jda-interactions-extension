package fr.alexpado.jda.interactions.responses;

import fr.alexpado.jda.interactions.meta.ChoiceMeta;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;

import java.util.List;

/**
 * Interface representing the default response for a {@link CommandAutoCompleteInteraction}.
 */
public interface AutoCompleteResponse {

    /**
     * Retrieve the {@link List} of {@link ChoiceMeta} that should be sent as response.
     *
     * @return A {@link List} of {@link ChoiceMeta}.
     */
    List<ChoiceMeta> getChoices();

}
