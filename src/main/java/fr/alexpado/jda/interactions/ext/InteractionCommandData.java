package fr.alexpado.jda.interactions.ext;

import fr.alexpado.jda.interactions.InteractionTools;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionCommandData extends CommandDataImpl {

    private final Map<String, InteractionGroupData>      groups      = new HashMap<>();
    private final Map<String, InteractionSubcommandData> subCommands = new HashMap<>();

    public InteractionCommandData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.getDescription());
    }

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

    public void register(InteractionMeta meta) {

        List<String> path = Arrays.asList(meta.getName().split("/"));

        boolean isOverflowing    = path.size() > 3;
        boolean hasGroupError    = path.size() == 2 && !this.groups.isEmpty();
        boolean hasSubGroupError = path.size() == 3 && !this.subCommands.isEmpty();

        if (isOverflowing || hasGroupError || hasSubGroupError) {
            throw new IllegalStateException(String.format("Invalid nesting for %s", meta.getName()));
        }

        if (path.size() == 1) {
            InteractionTools.registerOptions(this::addOptions, meta);
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
