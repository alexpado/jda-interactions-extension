package fr.alexpado.jda.interactions.entities;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.exceptions.InteractionDeclarationException;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InteractionItem implements fr.alexpado.jda.interactions.interfaces.interactions.InteractionItem {

    private final Object                 instance;
    private final Method                 method;
    private final InteractionMeta        meta;
    private final Predicate<Interaction> filter;
    private final List<Option>           options;
    private final boolean                hideResult;

    /**
     * Create a new {@link InteractionItem} instance.
     *
     * @param instance
     *         The object instance which will handle an interaction execution.
     * @param method
     *         The method to execute within the object instance when handling an interaction execution.
     * @param interact
     *         The {@link Button} annotation containing the main data describing the interaction.
     */
    public InteractionItem(Object instance, Method method, InteractionMeta interact) {

        this.instance = instance;
        this.method   = method;
        this.meta     = interact;
        this.options  = Collections.emptyList();

        this.filter = interaction -> {
            boolean targetAllowed = interact.getTarget().isCompatible(interaction);
            boolean typeAllowed   = interact.getType().isCompatible(interaction);
            return targetAllowed && typeAllowed;
        };

        this.hideResult = interact.isHidden();

        if (!this.method.getReturnType().equals(InteractionResponse.class)) {
            throw new InteractionDeclarationException(instance.getClass(), method, this.getName(), "The method does not return the InteractionResult class.");
        }
    }

    public static List<fr.alexpado.jda.interactions.interfaces.interactions.InteractionItem> of(Object instance, Method method, Interact interact) {

        return InteractionMeta.of(interact).stream()
                .map(meta -> new InteractionItem(interact, method, meta))
                .collect(Collectors.toList());
    }

    @Override
    public InteractionResponse execute(DispatchEvent event, Map<Class<?>, Function<Interaction, ?>> mapping) throws Exception {

        Collection<Object> callArgs = new ArrayList<>();

        for (Parameter parameter : this.method.getParameters()) {
            String paramType = parameter.getType().getSimpleName();

            if (parameter.isAnnotationPresent(Param.class)) {
                Param param = parameter.getAnnotation(Param.class);

                if (event.getOptions().containsKey(param.value())) {
                    callArgs.add(event.getOptions().get(param.value()));
                } else {
                    callArgs.add(this.provideOption(event, param.value()));
                }
            } else if (mapping.containsKey(parameter.getType())) {
                try {
                    callArgs.add(mapping.get(parameter.getType()).apply(event.getInteraction()));
                } catch (Exception e) {
                    throw new InteractionDeclarationException(e, this.instance.getClass(), this.method, this.getName(), "Unable to inject parameter " + paramType);
                }
            } else {
                throw new InteractionDeclarationException(this.instance.getClass(), this.method, this.getName(), "Unmapped parameter " + paramType);
            }

        }

        Object invoke = this.method.invoke(this.instance, callArgs.toArray(Object[]::new));

        if (invoke instanceof InteractionResponse) {
            return (InteractionResponse) invoke;
        }

        String returnType = invoke.getClass().getSimpleName();
        throw new InteractionDeclarationException(this.instance.getClass(), this.method, this.getName(), "Unsupported return type (" + returnType + ")");
    }

    @Override
    public boolean canExecute(Interaction interaction) {

        return this.filter.test(interaction);
    }

    public boolean isHidden() {

        return this.hideResult;
    }

    @Override
    public InteractionMeta getMeta() {

        return this.meta;
    }

    public String getPath() {

        return this.meta.getMetaName();
    }

    public String getName() {

        return this.meta.getName();
    }

    public Object provideOption(DispatchEvent event, String name) {

        if (event.getInteraction() instanceof CommandInteraction commandInteraction) {
            for (Option option : this.options) {

                if (!option.name().equals(name)) {
                    continue;
                }

                OptionMapping eventOption = commandInteraction.getOption(option.name());

                if (eventOption == null) {
                    if (option.required()) {
                        // This should never happen because Discord client disallows sending command without filling all options, but we never know.
                        throw new IllegalStateException("Option " + name + " wasn't provided by Discord API.");
                    }
                    return null;
                }

                return switch (option.type()) {
                    case STRING -> eventOption.getAsString();
                    case INTEGER -> eventOption.getAsLong();
                    case BOOLEAN -> eventOption.getAsBoolean();
                    case USER -> eventOption.getAsUser();
                    case CHANNEL -> eventOption.getAsGuildChannel();
                    case ROLE -> eventOption.getAsRole();
                    case MENTIONABLE -> eventOption.getAsMentionable();
                    default -> throw new InteractionDeclarationException(this.instance.getClass(), this.method, this.getName(), "Unsupported option " + option.name());
                };
            }
        }
        return null;
    }

    public Map<String, String> provideChoices(Interaction interaction, String name) {

        for (Option option : this.options) {
            if (option.name().equals(name)) {
                Map<String, String> choices = new HashMap<>();

                for (Choice choice : option.choices()) {
                    choices.put(choice.id(), choice.display());
                }

                return choices;
            }
        }

        return new HashMap<>();
    }


}
