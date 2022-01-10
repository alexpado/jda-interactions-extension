package fr.alexpado.jda.interactions.meta;

import fr.alexpado.jda.interactions.annotations.Option;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OptionMeta {

    private final String           name;
    private final String           description;
    private final List<ChoiceMeta> choices;
    private final boolean          required;
    private final OptionType       type;

    public OptionMeta(String name, String description, boolean required, OptionType type) {

        this(name, description, required, type, Collections.emptyList());
    }

    public OptionMeta(String name, String description, boolean required, OptionType type, ChoiceMeta... choices) {

        this(name, description, required, type, Arrays.asList(choices));
    }


    public OptionMeta(String name, String description, boolean required, OptionType type, List<ChoiceMeta> choices) {

        this.name        = name;
        this.description = description;
        this.choices     = choices;
        this.required    = required;
        this.type        = type;
    }

    public OptionMeta(Option option) {

        this.name        = option.name();
        this.description = option.description();
        this.choices     = Arrays.stream(option.choices()).map(ChoiceMeta::new).toList();
        this.required    = option.required();
        this.type        = option.type();
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public List<ChoiceMeta> getChoices() {

        return choices;
    }

    public boolean isRequired() {

        return required;
    }

    public OptionType getType() {

        return type;
    }

}
