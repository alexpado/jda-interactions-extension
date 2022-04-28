package fr.alexpado.jda.interactions.ext.discord;

import fr.alexpado.jda.interactions.meta.InteractionMeta;
import fr.alexpado.jda.interactions.meta.OptionMeta;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

/**
 * Bridging class allowing to plug this library logic into {@link JDA} to register slash interaction to Discord.
 */
public class InteractionSubcommandData extends SubcommandData {

    /**
     * Create a new {@link InteractionSubcommandData}.
     *
     * @param name
     *         The {@link Interaction} name (path).
     * @param meta
     *         The {@link InteractionMeta} containing info about the {@link Interaction}.
     */
    public InteractionSubcommandData(@NotNull String name, @NotNull InteractionMeta meta) {

        super(name, meta.getDescription());
        this.addOptions(meta.getOptions().stream().map(OptionMeta::createOptionData).toList());
    }

}
