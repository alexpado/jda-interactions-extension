package fr.alexpado.jda.interactions.ext.discord;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridging class allowing to plug this library logic into {@link JDA} to register slash interaction to Discord.
 */
public class InteractionCommandData extends CommandDataImpl {

    private final Map<String, InteractionGroupData>      groups      = new HashMap<>();
    private final Map<String, InteractionSubcommandData> subCommands = new HashMap<>();

    /**
     * Create a new {@link InteractionCommandData}.
     *
     * @param name
     *         The {@link Interaction} name (path).
     * @param meta
     *         The {@link InteractionMeta} containing info about the {@link Interaction}.
     */
    public InteractionCommandData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.description());
    }

    /**
     * Build all {@link JDA} objects based on the current command path tree registered through
     * {@link InteractionTarget}.
     */
    public void prepare() {

        if (!this.groups.isEmpty()) {
            for (InteractionGroupData value : this.groups.values()) {
                value.prepare();
                this.addSubcommandGroups(value);
            }
        } else if (!this.subCommands.isEmpty()) {
            for (InteractionSubcommandData value : this.subCommands.values()) {
                this.addSubcommands(value);
            }
        }
    }

    /**
     * Register the options of the provided {@link InteractionMeta}. If this requires additional command subject to be
     * created, it will create it.
     *
     * @param meta
     *         The {@link InteractionMeta} to register the option of.
     */
    public void register(InteractionMeta meta) {

        List<String> path = Arrays.asList(meta.name().split("/"));

        boolean isOverflowing    = path.size() > 3;
        boolean hasGroupError    = path.size() == 2 && !this.groups.isEmpty();
        boolean hasSubGroupError = path.size() == 3 && !this.subCommands.isEmpty();

        if (isOverflowing || hasGroupError || hasSubGroupError) {
            throw new IllegalStateException(String.format("Invalid nesting for %s", meta.name()));
        }

        if (path.size() == 1) {
            this.addOptions(meta.options().stream().map(OptionMeta::createOptionData).toList());
        } else if (path.size() == 2) {
            String                    name = path.get(1);
            InteractionSubcommandData data = this.subCommands.getOrDefault(name, new InteractionSubcommandData(name, meta));
            this.subCommands.put(name, data);
        } else if (path.size() == 3) {
            String               name = path.get(1);
            InteractionGroupData data = this.groups.getOrDefault(name, new InteractionGroupData(name, meta));
            data.register(meta);
            this.groups.put(name, data);
        }
    }

}
