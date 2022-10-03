package fr.alexpado.jda.interactions.responses;

import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;

import java.util.function.Consumer;

/**
 * Interface representing the default response for a {@link SlashInteractionTarget}.
 */
public interface SlashResponse {

    /**
     * Retrieve the {@link AbstractMessageBuilder} {@link Consumer} that should set the response content.
     *
     * @return A {@link AbstractMessageBuilder} {@link Consumer}
     */
    Consumer<AbstractMessageBuilder<?, ?>> getHandler();

    /**
     * Check if this {@link SlashResponse} is ephemeral (ie: Only shown to the user who interacted).
     *
     * @return True if ephemeral, false otherwise.
     */
    boolean isEphemeral();

}
