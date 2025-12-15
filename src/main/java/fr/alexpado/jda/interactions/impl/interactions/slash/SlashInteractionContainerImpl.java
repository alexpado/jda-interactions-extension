package fr.alexpado.jda.interactions.impl.interactions.slash;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.ext.discord.InteractionCommandData;
import fr.alexpado.jda.interactions.ext.sentry.ITimedAction;
import fr.alexpado.jda.interactions.impl.interactions.DefaultInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionContainer;
import fr.alexpado.jda.interactions.interfaces.interactions.slash.SlashInteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.responses.SlashResponse;
import fr.alexpado.jda.interactions.tools.InteractionUtils;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Class implementing {@link InteractionContainer} handling {@link SlashCommandInteraction} with target of type
 * {@link SlashInteractionTarget}.
 */
@Deprecated
public class SlashInteractionContainerImpl extends DefaultInteractionContainer<SlashInteractionTarget, SlashCommandInteraction> implements SlashInteractionContainer {

    private final Map<String, InteractionCommandData> dataMap;

    /**
     * Create a new {@link SlashInteractionContainerImpl} instance.
     */
    public SlashInteractionContainerImpl() {

        this.dataMap = new HashMap<>();
    }

    /**
     * Register the provided {@link InteractionTarget} into this {@link InteractionContainer}.
     *
     * @param target
     *         The {@link InteractionTarget} to register.
     *
     * @return True if it has been registered, false otherwise.
     */
    @Override
    public boolean register(SlashInteractionTarget target) {

        InteractionMeta meta   = target.getMeta();
        String          name   = meta.name();
        String          prefix = Arrays.asList(name.split("/")).getFirst();

        InteractionCommandData data = this.dataMap.getOrDefault(prefix, new InteractionCommandData(prefix, meta));
        data.register(meta);
        this.dataMap.put(prefix, data);

        return super.register(target);
    }

    /**
     * Retrieve the schema for the {@link URI} of each {@link InteractionTarget}.
     *
     * @return The schema
     */
    @Override
    public String getInteractionSchema() {

        return "slash";
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
    public URI getEventUri(SlashCommandInteraction event) {

        return this.createURI(event.getFullCommandName().replace(" ", "/"));
    }

    /**
     * Handle the provided event and wrap it in a {@link DispatchEvent}.
     *
     * @param event
     *         The Discord event
     *
     * @return A {@link DispatchEvent}.
     */
    @Override
    public DispatchEvent<SlashCommandInteraction> handle(ITimedAction timedAction, SlashCommandInteraction event) {

        URI uri = this.getEventUri(event);

        timedAction.action("read-options", "Reading command options");
        Map<String, Object> options = new HashMap<>();

        for (OptionMapping option : event.getOptions()) {
            options.put(option.getName(), InteractionUtils.extractOptionValue(option));
        }
        timedAction.endAction();

        return new DispatchEvent<>(timedAction, uri, event, options);
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

        return response instanceof SlashResponse;
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

        if (event.interaction() instanceof IReplyCallback callback && response instanceof SlashResponse slashResponse) {
            if (callback.isAcknowledged()) {
                event.timedAction().action("build", "Building the response");
                MessageEditBuilder builder = this.getMessageEditBuilder(slashResponse);
                event.timedAction().endAction();

                event.timedAction().action("reply", "Replying to the interaction (EDIT)");
                callback.getHook().editOriginal(builder.build()).complete();
                event.timedAction().endAction();
            } else {
                event.timedAction().action("build", "Building the response");
                MessageCreateBuilder builder = this.getMessageCreateBuilder(slashResponse);
                event.timedAction().endAction();

                event.timedAction().action("reply", "Replying to the interaction (CREATE)");
                callback.reply(builder.build()).setEphemeral(slashResponse.isEphemeral()).complete();
                event.timedAction().endAction();
            }
        }
    }

    private MessageEditBuilder getMessageEditBuilder(SlashResponse response) {

        MessageEditBuilder builder = new MessageEditBuilder();
        response.getHandler().accept(builder);
        return builder;
    }

    private MessageCreateBuilder getMessageCreateBuilder(SlashResponse response) {

        MessageCreateBuilder builder = new MessageCreateBuilder();
        response.getHandler().accept(builder);
        return builder;
    }

    /**
     * Convert and insert all {@link SlashInteractionTarget} into the {@link CommandListUpdateAction} provided and
     * returns it.
     *
     * @param action
     *         The {@link CommandListUpdateAction} into which commands will be inserted.
     *
     * @return The updated {@link CommandListUpdateAction}.
     */
    @Override
    public CommandListUpdateAction upsertCommands(CommandListUpdateAction action) {

        for (InteractionCommandData command : this.dataMap.values()) {
            command.prepare();
            //noinspection ResultOfMethodCallIgnored
            action.addCommands(command);
        }

        return action;
    }

}
