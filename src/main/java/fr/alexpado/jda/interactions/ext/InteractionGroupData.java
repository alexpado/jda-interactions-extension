package fr.alexpado.jda.interactions.ext;

import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionGroupData extends SubcommandGroupData {


    private final Map<String, InteractionSubcommandData> subCommands = new HashMap<>();

    public InteractionGroupData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.getDescription());
    }

    public void prepare() {

        if (!this.subCommands.isEmpty()) {
            for (InteractionSubcommandData value : this.subCommands.values()) {
                this.addSubcommands(value);
            }
        }
    }

    public void register(InteractionMeta interact) {

        List<String> path = Arrays.asList(interact.getName().split("/"));

        // Nesting checks
        if (path.size() > 3 || path.size() == 2) {
            throw new IllegalStateException(String.format("Invalid nesting for %s", interact.getName()));
        }

        if (path.size() == 3) {
            String                    name = path.get(2);
            InteractionSubcommandData data = this.subCommands.getOrDefault(name, new InteractionSubcommandData(name, interact));
            this.subCommands.put(name, data);
        }
    }

}
