package fr.alexpado.jda.interactions.executors;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.entities.responses.SimpleInteractionResponse;
import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.ext.InteractionCommandData;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.interactions.*;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

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

                List<InteractionItem> scannedItems = fr.alexpado.jda.interactions.entities.InteractionItem.of(candidate, interactiveMethod, interact);

                for (InteractionItem interactionItem : scannedItems) {
                    if (interactionItem.getMeta().getType() == InteractionType.SLASH) { // JDA objects shenanigans
                        String name   = interactionItem.getMeta().getName();
                        String prefix = Arrays.asList(name.split("/")).get(0);

                        InteractionCommandData data = this.dataMap.getOrDefault(prefix, new InteractionCommandData(prefix, interactionItem.getPath()));
                        data.register(interactionItem.getMeta());
                        this.dataMap.put(prefix, data);
                    }

                    this.items.add(interactionItem);
                }
            }
        }

        this.dataMap.values().forEach(InteractionCommandData::prepare);
    }

    @Override
    public final CommandListUpdateAction build(CommandListUpdateAction updateAction) {

        this.preprocess();
        for (InteractionCommandData value : this.dataMap.values()) {
            // Return value is just for chaining, let's ignore it.
            //noinspection ResultOfMethodCallIgnored
            updateAction.addCommands(value);
        }

        return updateAction;
    }

    @Override
    public List<InteractionItem> getInteractionItems() {

        return this.items;
    }

    @Override
    public final <T> void registerInteraction(T commandHolder) {

        this.candidates.add(commandHolder);
    }

    @Override
    public final void registerInteraction(InteractionMeta meta, ExecutableItem item) {

        String name   = meta.getName();
        String prefix = Arrays.asList(name.split("/")).get(0);

        InteractionCommandData data = this.preDataMap.getOrDefault(prefix, new InteractionCommandData(prefix, meta.getDescription()));
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

    @Override
    public final void registerInteraction(InteractionItem item) {

        this.preItems.add(item);
    }

    @Override
    public final boolean canResolve(URI uri) {

        return uri.getScheme().equals("button") || uri.getScheme().equals("slash");
    }

    @Override
    public final Optional<ExecutableItem> resolve(URI path) {

        String realPath = String.format("%s://%s%s", path.getScheme(), path.getHost(), path.getPath());

        return this.items.stream()
                .filter(item -> item.getPath().equals(realPath))
                .findFirst()
                .map(ExecutableItem.class::cast);
    }

    @Override
    public void prepare(DispatchEvent event) {

    }

    @Override
    public final boolean canHandle(InteractionResponse response) {

        return response instanceof SimpleInteractionResponse;
    }

    @Override
    public void handleResponse(DispatchEvent event, InteractionResponse response) {

        event.getInteraction().replyEmbeds(response.getEmbed().build()).setEphemeral(response.isEphemeral()).queue();
    }

}
