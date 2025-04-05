package fr.alexpado.jda.interactions.entities;

import fr.alexpado.jda.interactions.annotations.Choice;
import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.annotations.Param;
import fr.alexpado.jda.interactions.exceptions.InteractionDeclarationException;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionItem;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionManager;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponse;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InteractionItemImpl implements InteractionItem {

    private final Object                 instance;
    private final Method                 method;
    private final InteractionMeta        meta;
    private final Predicate<Interaction> filter;
    private final List<Option>           options;

    /**
     * Create a new {@link InteractionItemImpl} instance.
     *
     * @param instance
     *         The object instance which will handle an interaction execution.
     * @param method
     *         The method to execute within the object instance when handling an interaction execution.
     * @param interact
     *         The {@link Button} annotation containing the main data describing the interaction.
     */
    public InteractionItemImpl(Object instance, Method method, InteractionMeta interact) {

        this.instance = instance;
        this.method   = method;
        this.meta     = interact;
        this.options  = Collections.emptyList();

        this.filter = interaction -> {
            boolean targetAllowed = interact.getTarget().isCompatible(interaction);
            boolean typeAllowed   = interact.getType().isCompatible(interaction);
            return targetAllowed && typeAllowed;
        };

        if (!this.method.getReturnType().equals(InteractionResponse.class)) {
            throw new InteractionDeclarationException(
                    instance.getClass(),
                    method,
                    this.getName(),
                    "The method does not return the InteractionResult class."
            );
        }
    }

    public static List<InteractionItem> of(Object instance, Method method, Interact interact) {

        return InteractionMeta.of(interact).stream()
                              .map(meta -> new InteractionItemImpl(instance, method, meta))
                              .collect(Collectors.toList());
    }

    /**
     * Execute this {@link ExecutableItem} with the provided parameters.
     *
     * @param event
     *         The {@link DispatchEvent} that allowed to match this {@link ExecutableItem}.
     * @param mapping
     *         The dependency mapping set through {@link InteractionManager#registerMapping(Class, Function)}.
     *
     * @return An {@link InteractionResponse} implementation.
     *
     * @throws Exception
     *         Threw if something happen during the execution. Implementation dependent.
     */
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
                    throw new InteractionDeclarationException(
                            e,
                            this.instance.getClass(),
                            this.method,
                            this.getName(),
                            "Unable to inject parameter " + paramType
                    );
                }
            } else {
                throw new InteractionDeclarationException(
                        this.instance.getClass(),
                        this.method,
                        this.getName(),
                        "Unmapped parameter " + paramType
                );
            }

        }

        Object invoke = this.method.invoke(this.instance, callArgs.toArray(Object[]::new));

        if (invoke instanceof InteractionResponse) {
            return (InteractionResponse) invoke;
        }

        String returnType = invoke.getClass().getSimpleName();
        throw new InteractionDeclarationException(
                this.instance.getClass(),
                this.method,
                this.getName(),
                "Unsupported return type (" + returnType + ")"
        );
    }

    /**
     * Check if this {@link InteractionItem} can be used with the given {@link Interaction}. This is useful if you want to
     * restrict some actions to some guilds.
     *
     * @param interaction
     *         The Discord {@link Interaction}.
     *
     * @return True if executable, false otherwise.
     */
    @Override
    public boolean canExecute(Interaction interaction) {

        return this.filter.test(interaction);
    }

    /**
     * Retrieves this {@link InteractionItem}'s {@link InteractionMeta}.
     *
     * @return The {@link InteractionItem}'s {@link InteractionMeta}.
     */
    @Override
    public InteractionMeta getMeta() {

        return this.meta;
    }

    /**
     * Retrieves this {@link InteractionItem}'s URI as string.
     *
     * @return The {@link InteractionItem}'s URI.
     */
    @Override
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
                    case CHANNEL -> eventOption.getAsChannel();
                    case ROLE -> eventOption.getAsRole();
                    case MENTIONABLE -> eventOption.getAsMentionable();
                    case ATTACHMENT -> eventOption.getAsAttachment();
                    case NUMBER -> eventOption.getAsDouble();
                    default -> throw new InteractionDeclarationException(
                            this.instance.getClass(),
                            this.method,
                            this.getName(),
                            "Unsupported option " + option.name()
                    );
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
