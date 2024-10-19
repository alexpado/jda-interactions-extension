package fr.alexpado.jda.interactions.ext.discord;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionTarget;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridging class allowing to plug this library logic into {@link JDA} to register slash interaction to Discord.
 */
public class InteractionGroupData extends SubcommandGroupData {


    private final Map<String, InteractionSubcommandData> subCommands = new HashMap<>();

    /**
     * Create a new {@link InteractionGroupData}.
     *
     * @param name
     *         The {@link Interaction} name (path).
     * @param meta
     *         The {@link InteractionMeta} containing info about the {@link Interaction}.
     */
    public InteractionGroupData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.getDescription());
    }

    /**
     * Build all {@link JDA} objects based on the current command path tree registered through
     * {@link InteractionTarget}.
     */
    public void prepare() {

        if (!this.subCommands.isEmpty()) {
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

        List<String> path = Arrays.asList(meta.getName().split("/"));

        // Nesting checks
        if (path.size() > 3 || path.size() == 2) {
            throw new IllegalStateException(String.format("Invalid nesting for %s", meta.getName()));
        }

        if (path.size() == 3) {
            String                    name = path.get(2);
            InteractionSubcommandData data = this.subCommands.getOrDefault(name, new InteractionSubcommandData(name, meta));
            this.subCommands.put(name, data);
        }
    }

}
