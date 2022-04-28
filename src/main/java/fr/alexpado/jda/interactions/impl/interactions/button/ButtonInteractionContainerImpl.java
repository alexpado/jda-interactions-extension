package fr.alexpado.jda.interactions.impl.interactions.button;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.exceptions.InteractionNotFoundException;
import fr.alexpado.jda.interactions.impl.interactions.DefaultInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class implementing {@link InteractionContainer} handling {@link ButtonInteraction} with target of type
 * {@link ButtonInteractionTarget}.
 */
public class ButtonInteractionContainerImpl extends DefaultInteractionContainer<ButtonInteractionTarget, ButtonInteraction> implements ButtonInteractionContainer {

    /**
     * Retrieve the schema for the {@link URI} of each {@link InteractionTarget}.
     *
     * @return The schema
     */
    @Override
    public String getInteractionSchema() {

        return "button";
    }

    /**
     * Try to find an {@link InteractionTarget} matching the {@link URI}.
     *
     * @param uri
     *         The {@link URI} to match.
     *
     * @return An optional {@link InteractionTarget}
     */
    @Override
    public Optional<ButtonInteractionTarget> resolve(URI uri) {

        try {
            URI raw = new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null, // Ignore the query part of the input url
                    uri.getFragment());

            return super.resolve(raw);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Called when an {@link DispatchEvent} is being fired.
     *
     * @param event
     *         The {@link DispatchEvent}
     *
     * @return An object representing the interaction result.
     */
    @Override
    public Object dispatch(DispatchEvent<ButtonInteraction> event) throws Exception {

        Optional<ButtonInteractionTarget> optionalTarget = this.resolve(event.getPath());

        if (optionalTarget.isEmpty()) {
            throw new InteractionNotFoundException(this, event);
        }

        ButtonInteractionTarget target = optionalTarget.get();

        // Build Options
        Map<String, String> query = new HashMap<>();

        if (event.getPath().getQuery() != null) {
            String[] options = event.getPath().getQuery().split("&");
            for (String option : options) {
                String[] parts = option.split("=");
                query.put(parts[0], parts[1]);
            }
        }

        for (OptionMeta option : target.getMeta().getOptions()) {
            String name  = option.getName();
            String value = query.get(name);

            if (null == value) {
                if (option.isRequired()) {
                    throw new IllegalStateException(String.format("Option %s is required.", name));
                }
                event.getOptions().put(name, null);
                continue;
            }

            switch (option.getType()) {
                case STRING -> event.getOptions().put(name, value);
                case INTEGER -> event.getOptions().put(name, Long.parseLong(value));
                case BOOLEAN -> event.getOptions().put(name, Boolean.parseBoolean(value));
                case USER -> {
                    long id = Long.parseLong(value);
                    event.getOptions().put(name, event.getInteraction().getJDA().getUserById(id));
                }
                case CHANNEL -> {
                    long  id    = Long.parseLong(value);
                    Guild guild = event.getInteraction().getGuild();
                    if (null == guild) {
                        throw new IllegalStateException("Cannot load guild in a private channel context.");
                    }
                    event.getOptions().put(name, guild.getGuildChannelById(id));
                }
                case ROLE -> {
                    long  id    = Long.parseLong(value);
                    Guild guild = event.getInteraction().getGuild();
                    if (null == guild) {
                        throw new IllegalStateException("Cannot load guild in a private channel context.");
                    }
                    event.getOptions().put(name, guild.getRoleById(id));
                }
                case NUMBER -> event.getOptions().put(name, Double.parseDouble(value));
                default -> // Unsupported option through URI
                        event.getOptions().put(name, null);
            }
        }

        return target.execute(event, this.getMappedClasses());
    }

    /**
     * Generate the {@link URI} for the provided {@link Interaction}.
     *
     * @param event
     *         The Discord event
     *
     * @return An {@link URI}
     */
    @Override
    public URI getEventUri(ButtonInteraction event) {

        return this.createURI(event.getComponentId());
    }

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided response.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The object representing the response given by an interaction.
     *
     * @return True if able to handle, false otherwise.
     */
    @Override
    public <T extends Interaction> boolean canHandle(DispatchEvent<T> event, @Nullable Object response) {

        return response instanceof ButtonResponse;
    }

    /**
     * Handle the response resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the response.
     * @param response
     *         The {@link Object} to handle.
     */
    @Override
    public <T extends Interaction> void handleResponse(DispatchEvent<T> event, @Nullable Object response) {

        if (event.getInteraction() instanceof ButtonInteraction callback && response instanceof ButtonResponse buttonResponse) {

            event.getTimedAction().action("build", "Building the response");
            Message message = buttonResponse.getMessage();
            event.getTimedAction().endAction();

            event.getTimedAction().action("replying", "Sending the reply");
            if (callback.isAcknowledged()) {
                if (buttonResponse.shouldEditOriginalMessage()) {
                    callback.getHook().editOriginal(message).complete();
                } else {
                    callback.getHook().sendMessage(message).complete();
                }
            } else {
                if (buttonResponse.shouldEditOriginalMessage()) {
                    callback.editMessage(message).complete();
                } else {
                    callback.reply(message).complete();
                }
            }
            event.getTimedAction().endAction();
        }
    }
}
