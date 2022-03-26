package fr.alexpado.jda.interactions.interfaces.bridge;

import fr.alexpado.jda.interactions.enums.InteractionType;
import fr.alexpado.jda.interactions.enums.SlashTarget;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Yes, duplicated code, because the JVM doesn't allow {@link SlashCommandInteraction} and {@link ButtonInteraction} to
 * be cast to {@link JdaInteraction}, even though both interfaces implement the required interfaces {@link Interaction}
 * and {@link IReplyCallback}...
 *
 * And obviously with this, we're losing the ability to use 'instanceof', so I had to create a method for this...
 */
@SuppressWarnings("DuplicatedCode")
public interface JdaInteraction extends Interaction, IReplyCallback {

    InteractionType getInteractionType();

    static JdaInteraction from(SlashCommandInteraction interaction) {

        return new JdaInteraction() {

            @NotNull
            @Override
            public ReplyCallbackAction deferReply() {

                return interaction.deferReply();
            }

            @NotNull
            @Override
            public InteractionHook getHook() {

                return interaction.getHook();
            }

            @Override
            public int getTypeRaw() {

                return interaction.getTypeRaw();
            }

            @NotNull
            @Override
            public String getToken() {

                return interaction.getToken();
            }

            @Nullable
            @Override
            public Guild getGuild() {

                return interaction.getGuild();
            }

            @NotNull
            @Override
            public User getUser() {

                return interaction.getUser();
            }

            @Nullable
            @Override
            public Member getMember() {

                return interaction.getMember();
            }

            @Override
            public @NotNull Channel getChannel() {

                return interaction.getChannel();
            }

            @Override
            public boolean isAcknowledged() {

                return interaction.isAcknowledged();
            }

            @NotNull
            @Override
            public Locale getUserLocale() {

                return interaction.getUserLocale();
            }

            @NotNull
            @Override
            public JDA getJDA() {

                return interaction.getJDA();
            }

            @Override
            public long getIdLong() {

                return interaction.getIdLong();
            }

            @Override
            public InteractionType getInteractionType() {

                return InteractionType.SLASH;
            }
        };
    }

    static JdaInteraction from(ButtonInteraction interaction) {

        return new JdaInteraction() {

            @NotNull
            @Override
            public ReplyCallbackAction deferReply() {

                return interaction.deferReply();
            }

            @NotNull
            @Override
            public InteractionHook getHook() {

                return interaction.getHook();
            }

            @Override
            public int getTypeRaw() {

                return interaction.getTypeRaw();
            }

            @NotNull
            @Override
            public String getToken() {

                return interaction.getToken();
            }

            @Nullable
            @Override
            public Guild getGuild() {

                return interaction.getGuild();
            }

            @NotNull
            @Override
            public User getUser() {

                return interaction.getUser();
            }

            @Nullable
            @Override
            public Member getMember() {

                return interaction.getMember();
            }

            @Override
            public @NotNull Channel getChannel() {

                return interaction.getChannel();
            }

            @Override
            public boolean isAcknowledged() {

                return interaction.isAcknowledged();
            }

            @NotNull
            @Override
            public Locale getUserLocale() {

                return interaction.getUserLocale();
            }

            @NotNull
            @Override
            public JDA getJDA() {

                return interaction.getJDA();
            }

            @Override
            public long getIdLong() {

                return interaction.getIdLong();
            }

            @Override
            public InteractionType getInteractionType() {

                return InteractionType.BUTTON;
            }
        };
    }

}
