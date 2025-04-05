package fr.alexpado.jda.interactions.executors;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.entities.InteractionItemImpl;
import fr.alexpado.jda.interactions.entities.responses.SimpleInteractionResponse;
import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.ext.InteractionCommandData;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class BasicDiscordContainer implements InteractionExecutor, InteractionContainer, InteractionResponseHandler {

    private final Collection<Object>                  candidates;
    private final List<InteractionItem>               items;
    private final Map<String, InteractionCommandData> dataMap;

    // Pre-Preprocess memory
    private final List<InteractionItem>               preItems;
    private final Map<String, InteractionCommandData> preDataMap;

    public BasicDiscordContainer() {

        this.candidates = new ArrayList<>();
        this.items      = new ArrayList<>();
        this.dataMap    = new HashMap<>();
        this.preItems   = new ArrayList<>();
        this.preDataMap = new HashMap<>();
    }

    private void preprocess() {

        this.items.clear();
        this.dataMap.clear();

        this.items.addAll(this.preItems);
        this.dataMap.putAll(this.preDataMap);

        for (Object candidate : this.candidates) {

            Class<?> clazz   = candidate.getClass();
            Method[] methods = clazz.getMethods();

            List<Method> interactiveMethods = Arrays.stream(methods)
                                                    .filter(method -> method.isAnnotationPresent(Interact.class))
                                                    .toList();

            for (Method interactiveMethod : interactiveMethods) {
                Interact interact = interactiveMethod.getAnnotation(Interact.class);

                List<InteractionItem> scannedItems = InteractionItemImpl.of(candidate, interactiveMethod, interact);

                for (InteractionItem interactionItem : scannedItems) {
                    if (interactionItem.getMeta().getType() == InteractionType.SLASH) { // JDA objects shenanigans
                        String name   = interactionItem.getMeta().getName();
                        String prefix = Arrays.asList(name.split("/")).get(0);

                        InteractionCommandData data = this.dataMap.getOrDefault(
                                prefix,
                                new InteractionCommandData(
                                        prefix,
                                        interactionItem.getMeta()
                                )
                        );
                        data.register(interactionItem.getMeta());
                        this.dataMap.put(prefix, data);
                    }

                    this.items.add(interactionItem);
                }
            }
        }

        this.dataMap.values().forEach(InteractionCommandData::prepare);
    }

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
    @Override
    public CommandListUpdateAction build(CommandListUpdateAction updateAction) {

        this.preprocess();
        for (InteractionCommandData value : this.dataMap.values()) {
            // Return value is just for chaining, let's ignore it.
            //noinspection ResultOfMethodCallIgnored
            updateAction.addCommands(value);
        }

        return updateAction;
    }

    /**
     * Get all {@link InteractionItem} present in this {@link InteractionContainer}.
     *
     * @return A list of {@link InteractionItem}.
     */
    @Override
    public List<InteractionItem> getInteractionItems() {

        return this.items;
    }

    /**
     * Add the provided object to a list of objects to scan when {@link #build(CommandListUpdateAction)} will be called.
     * <p>
     * The object must have at least one public method annotated with {@link Interact} for this call to serve a purpose.
     *
     * @param holder
     *         The object to add.
     */
    @Override
    public void registerInteraction(Object holder) {

        this.candidates.add(holder);
    }

    /**
     * Register a new {@link ExecutableItem} with the provided {@link InteractionMeta}. This allows to register simple interaction
     * on-the-fly without bothering with annotations.
     *
     * @param meta
     *         The {@link InteractionMeta} of the new interaction.
     * @param item
     *         The {@link ExecutableItem} to use when executing the interaction.
     */
    @Override
    public void registerInteraction(InteractionMeta meta, ExecutableItem item) {

        String name   = meta.getName();
        String prefix = Arrays.asList(name.split("/")).get(0);

        InteractionCommandData data = this.preDataMap.getOrDefault(prefix, new InteractionCommandData(prefix, meta));
        data.register(meta);
        this.preDataMap.put(prefix, data);

        this.registerInteraction(new InteractionItem() {

            @Override
            public InteractionMeta getMeta() {

                return meta;
            }

            @Override
            public String getPath() {

                return meta.getMetaName();
            }

            @Override
            public boolean canExecute(Interaction interaction) {

                boolean targetAllowed = meta.getTarget().isCompatible(interaction);
                boolean typeAllowed   = meta.getType().isCompatible(interaction);
                return targetAllowed && typeAllowed;

            }

            @Override
            public InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<Interaction, ?>> mapping) throws Exception {

                return item.execute(event, mapping);
            }
        });
    }

    /**
     * Register a new {@link InteractionItem}.
     *
     * @param item
     *         The {@link InteractionItem}.
     */
    @Override
    public void registerInteraction(InteractionItem item) {

        this.preItems.add(item);
    }

    /**
     * Check if this {@link InteractionExecutor} can be used to retrieve an {@link ExecutableItem} with the given URI.
     *
     * @param uri
     *         The {@link ExecutableItem} URI.
     *
     * @return True if this {@link InteractionExecutor} can handle the request.
     */
    @Override
    public boolean canResolve(URI uri) {

        return uri.getScheme().equals("button") || uri.getScheme().equals("slash");
    }

    /**
     * Try to match an {@link ExecutableItem} with the provided URI.
     *
     * @param path
     *         The {@link ExecutableItem} URI.
     *
     * @return An optional {@link ExecutableItem}.
     */
    @Override
    public Optional<ExecutableItem> resolve(URI path) {

        String realPath = String.format("%s://%s%s", path.getScheme(), path.getHost(), path.getPath());

        return this.items.stream()
                         .filter(item -> item.getPath().equals(realPath))
                         .findFirst()
                         .map(ExecutableItem.class::cast);
    }

    /**
     * Called when the {@link DispatchEvent} is ready and is about to be used on an {@link ExecutableItem}. Here you can add
     * custom options.
     *
     * @param event
     *         The {@link DispatchEvent} that will be used.
     */
    @Override
    public void prepare(DispatchEvent event) {

    }

    /**
     * Check if this {@link InteractionResponseHandler} can handle the provided {@link InteractionResponse}.
     *
     * @param response
     *         The generated {@link InteractionResponse}.
     *
     * @return True if able to handle, false otherwise.
     */
    @Override
    public boolean canHandle(InteractionResponse response) {

        return response instanceof SimpleInteractionResponse;
    }

    /**
     * Handle the {@link InteractionResponse} resulting from the {@link DispatchEvent} event provided.
     *
     * @param event
     *         The {@link DispatchEvent} source of the {@link InteractionResponse}.
     * @param executable
     *         The {@link ExecutableItem} that has been used to generate the {@link InteractionResponse}.
     * @param response
     *         The {@link InteractionResponse} to handle.
     */
    @Override
    public void handleResponse(DispatchEvent event, ExecutableItem executable, InteractionResponse response) {

        MessageEmbed embed = response.getEmbed().build();

        if (event.getInteraction().isAcknowledged() && event.getInteraction() instanceof IDeferrableCallback cb) {
            MessageEditBuilder meb = new MessageEditBuilder();
            meb.setEmbeds(embed);
            cb.getHook().editOriginal(meb.build()).queue();
            return;
        }

        boolean ephemeral;

        if (this.canResolve(event.getPath()) && executable instanceof InteractionItem item) {
            ephemeral = item.getMeta().isHidden();
        } else {
            ephemeral = response.isEphemeral();
        }

        if (event.getInteraction() instanceof ReplyCallbackAction cb) {
            cb.setEmbeds(embed)
              .setEphemeral(ephemeral)
              .queue();
            return;
        }

        throw new UnsupportedOperationException("Cannot handle response for interaction " + event.getInteraction()
                                                                                                 .getClass()
                                                                                                 .getName());
    }

}
