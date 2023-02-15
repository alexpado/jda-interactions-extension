package fr.alexpado.jda.interactions.impl.interactions.button;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.exceptions.InteractionNotFoundException;
import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.impl.interactions.DefaultInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.button.ButtonInteractionTarget;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import fr.alexpado.jda.interactions.responses.ButtonResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

        event.getTimedAction().action("resolve", "Finding the interaction target");
        Optional<ButtonInteractionTarget> optionalTarget = this.resolve(event.getPath());

        if (optionalTarget.isEmpty()) {
            throw new InteractionNotFoundException(this, event);
        }
        event.getTimedAction().endAction();

        ButtonInteractionTarget target = optionalTarget.get();

        // Build Options
        event.getTimedAction().action("convert", "Converting URI to interaction options");
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
        event.getTimedAction().endAction();

        event.getTimedAction().action("execute", "Running the interaction target");
        Object obj = target.execute(event, this.getMappedClasses());
        event.getTimedAction().endAction();
        return obj;
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

        if (event.getComponentId().matches(".*://.*")) {
            return URI.create(event.getComponentId());
        }
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
            if (callback.isAcknowledged()) {
                this.doResponseHandling(event.getTimedAction(), buttonResponse, (builder) -> {
                    callback.getHook().editOriginal(builder.build()).complete();
                }, (builder) -> {
                    callback.getHook()
                            .sendMessage(builder.build())
                            .setEphemeral(buttonResponse.isEphemeral())
                            .complete();
                });
            } else {
                this.doResponseHandling(event.getTimedAction(), buttonResponse, (builder) -> {
                    callback.editMessage(builder.build()).complete();
                }, (builder) -> {
                    callback.reply(builder.build()).setEphemeral(buttonResponse.isEphemeral()).complete();
                });
            }
        }
    }

    private void doResponseHandling(ITimedAction action, ButtonResponse response, Consumer<MessageEditBuilder> editCall, Consumer<MessageCreateBuilder> createCall) {

        if (response.shouldEditOriginalMessage()) {
            action.action("build", "Building the response");
            MessageEditBuilder builder = this.getMessageEditBuilder(response);
            action.endAction();

            action.action("reply", "Replying to the interaction (EDIT)");
            editCall.accept(builder);
            action.endAction();
        } else {
            action.action("build", "Building the response");
            MessageCreateBuilder builder = this.getMessageCreateBuilder(response);
            action.endAction();

            action.action("reply", "Replying to the interaction (CREATE)");
            createCall.accept(builder);
            action.endAction();
        }
    }

    private MessageEditBuilder getMessageEditBuilder(ButtonResponse response) {

        MessageEditBuilder builder = new MessageEditBuilder();
        response.getHandler().accept(builder);
        return builder;
    }

    private MessageCreateBuilder getMessageCreateBuilder(ButtonResponse response) {

        MessageCreateBuilder builder = new MessageCreateBuilder();
        response.getHandler().accept(builder);
        return builder;
    }

}
