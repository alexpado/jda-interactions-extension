package fr.alexpado.interactions.providers.interactions.slash;

import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.interactions.slash.adapters.SlashSchemeAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlashSchemeAdapterTests {

    @InjectMocks
    private SlashSchemeAdapter adapter;

    @Mock
    private SlashCommandInteraction event;

    @Test
    @DisplayName("createRequest() should map command path to slash URI")
    void createRequest_shouldMapPath() {

        when(this.event.getFullCommandName()).thenReturn("group subcommand command");

        Optional<Request<SlashCommandInteraction>> result = this.adapter.createRequest(this.event);

        assertTrue(result.isPresent());
        assertEquals(URI.create("slash://group/subcommand/command"), result.get().getUri());
    }

    @Test
    @DisplayName("createRequest() should map options to parameters")
    void createRequest_shouldMapOptions() {

        when(this.event.getFullCommandName()).thenReturn("command");

        OptionMapping strOpt = mock(OptionMapping.class);
        when(strOpt.getName()).thenReturn("arg1");
        when(strOpt.getType()).thenReturn(OptionType.STRING);
        when(strOpt.getAsString()).thenReturn("value");

        OptionMapping intOpt = mock(OptionMapping.class);
        when(intOpt.getName()).thenReturn("arg2");
        when(intOpt.getType()).thenReturn(OptionType.INTEGER);
        when(intOpt.getAsLong()).thenReturn(100L);

        when(this.event.getOptions()).thenReturn(List.of(strOpt, intOpt));

        Request<SlashCommandInteraction> req = this.adapter.createRequest(this.event).orElseThrow();

        assertEquals("value", req.getParameters().get("arg1"));
        assertEquals(100L, req.getParameters().get("arg2"));
    }

}
