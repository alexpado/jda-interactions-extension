package fr.alexpado.jda.interactions.meta;

import fr.alexpado.jda.interactions.annotations.Interact;
import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.enums.SlashTarget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InteractionMeta {

    private final String           name;
    private final String           description;
    private final InteractionType  type;
    private final SlashTarget      target;
    private final List<OptionMeta> options;
    private final boolean          hide;
    private final boolean          defer;

    public InteractionMeta(String name, String description, InteractionType type, SlashTarget target, boolean hide, boolean defer) {

        this(name, description, type, target, hide, defer, Collections.emptyList());
    }

    public InteractionMeta(String name, String description, InteractionType type, SlashTarget target, boolean hide, boolean defer, OptionMeta... options) {

        this(name, description, type, target, hide, defer, Arrays.asList(options));
    }


    public InteractionMeta(String name, String description, InteractionType type, SlashTarget target, boolean hide, boolean defer, List<OptionMeta> options) {

        this.name        = name;
        this.description = description;
        this.type        = type;
        this.target      = target;
        this.options     = options;
        this.hide        = hide;
        this.defer       = defer;
    }

    public InteractionMeta(InteractionType type, Interact interact) {

        this.name        = interact.name();
        this.description = interact.description();
        this.type        = type;
        this.target      = interact.target();
        this.options     = Arrays.stream(interact.options()).map(OptionMeta::new).toList();

        this.hide = switch (type) {
            case ALL -> throw new IllegalStateException("ALL is not supported type as InteractionMeta");
            case SLASH -> interact.hideAsSlash();
            case BUTTON -> interact.hideAsButton();
            case NONE -> false;
        };

        this.defer = interact.defer();
    }

    public static List<InteractionMeta> of(Interact interact) {

        return switch (interact.type()) {
            case NONE, SLASH, BUTTON -> Collections.singletonList(new InteractionMeta(interact.type(), interact));
            case ALL -> Arrays.asList(
                    new InteractionMeta(InteractionType.SLASH, interact),
                    new InteractionMeta(InteractionType.BUTTON, interact)
            );
        };
    }

    public String getName() {

        return name;
    }

    public String getMetaName() {

        return this.type.withPrefix(this.getName());
    }

    public String getDescription() {

        return description;
    }

    public InteractionType getType() {

        return type;
    }

    public SlashTarget getTarget() {

        return target;
    }

    public List<OptionMeta> getOptions() {

        return options;
    }

    public boolean isHidden() {

        return hide;
    }

    public boolean isDeferred() {

        return defer;
    }

}
