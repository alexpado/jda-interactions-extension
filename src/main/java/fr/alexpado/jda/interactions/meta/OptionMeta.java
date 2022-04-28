package fr.alexpado.jda.interactions.meta;

import fr.alexpado.jda.interactions.annotations.Option;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class version of the annotation {@link Option}.
 */
public class OptionMeta {

    private final String           name;
    private final String           description;
    private final List<ChoiceMeta> choices;
    private final boolean          required;
    private final boolean          autoCompletable;
    private final OptionType       type;

    /**
     * Create a new {@link OptionMeta}.
     *
     * @param name
     *         The option name
     * @param description
     *         The option description
     * @param required
     *         Define if the option is required. For {@link SlashCommandInteraction}, it will prevent the user to issue
     *         the command if not all required options are set. For {@link ButtonInteraction}, it will prevent the
     *         execution of the associated {@link InteractionTarget} with an exception.
     * @param autoCompletable
     *         Define if the option is auto-completable.
     * @param type
     *         The option type.
     */
    public OptionMeta(String name, String description, boolean required, boolean autoCompletable, OptionType type) {

        this(name, description, required, autoCompletable, type, Collections.emptyList());
    }

    /**
     * Create a new {@link OptionMeta}.
     *
     * @param name
     *         The option name
     * @param description
     *         The option description
     * @param required
     *         Define if the option is required. For {@link SlashCommandInteraction}, it will prevent the user to issue
     *         the command if not all required options are set. For {@link ButtonInteraction}, it will prevent the
     *         execution of the associated {@link InteractionTarget} with an exception.
     * @param autoCompletable
     *         Define if the option is auto-completable.
     * @param type
     *         The option type.
     * @param choices
     *         The option {@link ChoiceMeta} list.
     */
    public OptionMeta(String name, String description, boolean required, boolean autoCompletable, OptionType type, List<ChoiceMeta> choices) {

        this.name            = name;
        this.description     = description;
        this.choices         = choices;
        this.required        = required;
        this.autoCompletable = autoCompletable;
        this.type            = type;
    }

    /**
     * Create a new {@link OptionMeta}.
     *
     * @param option
     *         The {@link Option} annotation from which data will be loaded.
     */
    public OptionMeta(Option option) {

        this.name            = option.name();
        this.description     = option.description();
        this.choices         = Arrays.stream(option.choices()).map(ChoiceMeta::new).toList();
        this.required        = option.required();
        this.autoCompletable = option.autoComplete();
        this.type            = option.type();
    }

    /**
     * Retrieve this {@link OptionMeta} name.
     *
     * @return The name.
     */
    public String getName() {

        return this.name;
    }

    /**
     * Retrieve this {@link OptionMeta} description.
     *
     * @return The description.
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Retrieve this {@link OptionMeta} {@link ChoiceMeta} list.
     *
     * @return A list of {@link ChoiceMeta}.
     */
    public List<ChoiceMeta> getChoices() {

        return this.choices;
    }

    /**
     * Check if this {@link OptionMeta} is required to be set to execute the {@link InteractionTarget}.
     *
     * @return True if required, false otherwise.
     */
    public boolean isRequired() {

        return this.required;
    }

    /**
     * Check if this {@link OptionMeta} is auto-completable.
     *
     * @return True if auto-completable, false otherwise.
     */
    public boolean isAutoCompletable() {

        return this.autoCompletable;
    }

    /**
     * Retrieve this {@link OptionMeta} {@link OptionType}.
     *
     * @return The {@link OptionType}.
     */
    public OptionType getType() {

        return this.type;
    }

    /**
     * Create the associated {@link OptionData} of this {@link OptionMeta}.
     *
     * @return An {@link OptionData}.
     */
    public OptionData createOptionData() {

        OptionData optionData = new OptionData(this.getType(), this.getName(), this.getDescription(), this.isRequired(), this.isAutoCompletable());

        for (ChoiceMeta choice : this.getChoices()) {
            if (OptionType.INTEGER == this.getType()) {
                optionData.addChoice(choice.getDisplay(), Integer.parseInt(choice.getId()));
            } else {
                optionData.addChoice(choice.getDisplay(), choice.getId());
            }
        }

        return optionData;
    }

}
