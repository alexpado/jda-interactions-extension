package fr.alexpado.interactions.interfaces;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * An adapter responsible for interpreting a raw JDA Interaction event and converting it into a standardized Request
 * object if it matches a specific scheme.
 *
 * @param <T>
 *         The type of JDA Interaction this adapter can handle.
 */
public interface SchemeAdapter<T extends Interaction> {

    /**
     * Add most common attachments to the {@link Request}. This method will use the {@link Request#getEvent()} to add
     * commons entities to the attachment map. More specific attachments should be handled at the {@link SchemeAdapter}
     * implementation level.
     *
     * @param request
     *         The {@link Request} for which the attachment map should be built.
     */
    static void buildAttachments(Request<?> request) {

        Interaction event = request.getEvent();

        request.addAttachment(JDA.class, event.getJDA());
        request.addAttachment(User.class, event.getUser());

        if (event.getChannel() != null) {
            request.addAttachment(Channel.class, event.getChannel());
        }

        if (event.getGuild() != null) {
            request.addAttachment(Guild.class, event.getGuild());
        }

        if (event.getMember() != null) {
            request.addAttachment(Member.class, event.getMember());
        }
    }

    /**
     * Attempts to create a Request from the given JDA event.
     * <p>
     * An implementation should check if the event's data (e.g., component ID, command name) corresponds to the scheme
     * it's designed to handle.
     *
     * @param event
     *         The incoming JDA event.
     *
     * @return An {@link Optional} containing the created {@link Request} if the event is a match, otherwise
     *         {@link Optional#empty()}.
     */
    Optional<Request<T>> createRequest(@NotNull T event);

}
