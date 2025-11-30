package fr.alexpado.interactions.interfaces.handlers;

import fr.alexpado.interactions.interfaces.SchemeAdapter;
import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for a global error handler that catches and processes any exception occurring during the interaction
 * lifecycle.
 */
public interface ErrorHandler {

    /**
     * Handles a throwable caught during the processing of an interaction.
     * <p>
     * This method is responsible for logging the error and/or sending a response back to the user on Discord.
     *
     * @param throwable
     *         The throwable that was caught.
     * @param interaction
     *         The original JDA {@link Interaction} event. This is always available and serves as the ultimate fallback
     *         for context.
     * @param request
     *         The {@link Request} object. This will be present if the error occurred after the request was successfully
     *         created by a {@link SchemeAdapter}.
     */
    void handle(@NotNull Throwable throwable, @NotNull Interaction interaction, @Nullable Request<?> request);

}
