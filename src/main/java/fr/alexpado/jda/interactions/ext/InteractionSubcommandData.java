package fr.alexpado.jda.interactions.ext;

import fr.alexpado.jda.interactions.InteractionTools;
import fr.alexpado.jda.interactions.meta.InteractionMeta;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public class InteractionSubcommandData extends SubcommandData {

    public InteractionSubcommandData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.getDescription());
        InteractionTools.registerOptions(this::addOptions, meta);
    }

}
