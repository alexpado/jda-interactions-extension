package fr.alexpado.interactions.providers.interactions.slash;

import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.interactions.slash.adapters.AutocompleteSchemeAdapter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutocompleteSchemeAdapterTests {

    @InjectMocks
    private AutocompleteSchemeAdapter adapter;

    @Mock
    private CommandAutoCompleteInteraction event;

    @Mock
    private AutoCompleteQuery focusedOption;

    @Mock
    private MessageChannelUnion channel;

    @Test
    @DisplayName("createRequest() should construct correct URI based on command path and focused option")
    void createRequest_shouldConstructCorrectUri() {
        // Arrange
        when(this.event.getFullCommandName()).thenReturn("group subcommand command");
        when(this.event.getFocusedOption()).thenReturn(this.focusedOption);
        when(this.focusedOption.getName()).thenReturn("targetOption");

        // Mock dependencies for buildAttachments
        when(this.event.getJDA()).thenReturn(mock(JDA.class));
        when(this.event.getUser()).thenReturn(mock(User.class));
        when(this.event.getChannel()).thenReturn(this.channel);

        // Act
        Optional<Request<CommandAutoCompleteInteraction>> result = this.adapter.createRequest(this.event);

        // Assert
        assertTrue(result.isPresent());
        Request<CommandAutoCompleteInteraction> request = result.get();
        assertEquals(URI.create("completion://group/subcommand/command/targetOption"), request.getUri());
    }

    @Test
    @DisplayName("createRequest() should populate parameters with other options but exclude the focused one")
    void createRequest_shouldPopulateParameters() {
        // Arrange
        when(this.event.getFullCommandName()).thenReturn("command");
        when(this.event.getFocusedOption()).thenReturn(this.focusedOption);
        when(this.focusedOption.getName()).thenReturn("focused");

        OptionMapping otherOption = mock(OptionMapping.class);
        when(otherOption.getName()).thenReturn("other");
        when(otherOption.getAsString()).thenReturn("otherValue");

        OptionMapping focusedMapping = mock(OptionMapping.class);
        when(focusedMapping.getName()).thenReturn("focused");
        // getAsString shouldn't be called for the focused option in the loop

        when(this.event.getOptions()).thenReturn(List.of(otherOption, focusedMapping));

        // Mock dependencies for buildAttachments
        when(this.event.getJDA()).thenReturn(mock(JDA.class));
        when(this.event.getUser()).thenReturn(mock(User.class));

        // Act
        Request<CommandAutoCompleteInteraction> request = this.adapter.createRequest(this.event).orElseThrow();

        // Assert
        assertEquals("otherValue", request.getParameters().get("other"));
        assertFalse(request.getParameters().containsKey("focused"), "Focused option should not be in parameters map");
    }

    @Test
    @DisplayName("createRequest() should add MessageChannel attachment")
    void createRequest_shouldAddAttachments() {
        // Arrange
        when(this.event.getFullCommandName()).thenReturn("cmd");
        when(this.event.getFocusedOption()).thenReturn(this.focusedOption);
        when(this.focusedOption.getName()).thenReturn("opt");

        when(this.event.getJDA()).thenReturn(mock(JDA.class));
        when(this.event.getUser()).thenReturn(mock(User.class));
        when(this.event.getChannel()).thenReturn(this.channel);

        // Act
        Request<CommandAutoCompleteInteraction> request = this.adapter.createRequest(this.event).orElseThrow();

        // Assert
        assertEquals(this.channel, request.getAttachment(MessageChannel.class));
    }

}

