package fr.alexpado.interactions.providers.interactions.slash;

import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.providers.interactions.slash.handlers.CompletionRouteHandler;
import fr.alexpado.interactions.providers.interactions.slash.interfaces.CompletionProvider;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompletionRouteHandlerTests {

    @Mock
    private CompletionProvider provider;

    @Mock
    private Request<CommandAutoCompleteInteraction> request;

    @Mock
    private CommandAutoCompleteInteraction event;

    @Test
    @DisplayName("handle() should filter choices based on user input")
    void handle_shouldFilterChoices() {
        // Arrange
        CompletionRouteHandler handler = new CompletionRouteHandler(this.provider);

        AutoCompleteQuery query = mock(AutoCompleteQuery.class);
        when(query.getValue()).thenReturn("ban"); // User typed "ban"

        when(this.request.getEvent()).thenReturn(this.event);
        when(this.event.getFocusedOption()).thenReturn(query);

        Command.Choice c1 = new Command.Choice("Banana", "banana");
        Command.Choice c2 = new Command.Choice("Apple", "apple");
        Command.Choice c3 = new Command.Choice("Bandana", "bandana");

        when(this.provider.complete(this.request)).thenReturn(Stream.of(c1, c2, c3));

        // Act
        List<Command.Choice> results = (List<Command.Choice>) handler.handle(this.request);

        // Assert
        assertEquals(2, results.size());
        assertEquals("Banana", results.get(0).getName());
        assertEquals("Bandana", results.get(1).getName());
    }

}
