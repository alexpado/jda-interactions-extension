package fr.alexpado.jda.interactions.ext;

import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionCommandData extends CommandData {

    private final Map<String, InteractionGroupData>      groups          = new HashMap<>();
    private final Map<String, InteractionSubcommandData> subCommands     = new HashMap<>();

    public InteractionCommandData(@NotNull String name, @NotNull String description) {

        super(name, description);
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

    public void register(InteractionMeta interact) {

        List<String> path = Arrays.asList(interact.getName().split("/"));

        boolean isOverflowing      = path.size() > 3;
        boolean hasGroupError      = path.size() == 2 && !this.groups.isEmpty();
        boolean hasSubGroupError   = path.size() == 3 && !this.subCommands.isEmpty();

        if (isOverflowing || hasGroupError || hasSubGroupError) {
            throw new IllegalStateException(String.format("Invalid nesting for %s", interact.getName()));
        }

        if (path.size() == 2) {
            String                    name = path.get(1);
            InteractionSubcommandData data = this.subCommands.getOrDefault(name, new InteractionSubcommandData(name, interact));
            this.subCommands.put(name, data);
        } else if (path.size() == 3) {
            String               name = path.get(1);
            InteractionGroupData data = this.groups.getOrDefault(name, new InteractionGroupData(name, interact));
            data.register(interact);
            this.groups.put(name, data);
        }
    }

}
