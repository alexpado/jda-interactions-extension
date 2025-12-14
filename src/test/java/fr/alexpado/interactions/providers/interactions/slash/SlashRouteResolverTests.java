package fr.alexpado.interactions.providers.interactions.slash;

import fr.alexpado.interactions.annotations.Choice;
import fr.alexpado.interactions.annotations.Completion;
import fr.alexpado.interactions.annotations.Option;
import fr.alexpado.interactions.annotations.Slash;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SlashRouteResolverTests {

    @Mock
    private Request<SlashCommandInteraction> slashRequest;

    @Mock
    private Request<CommandAutoCompleteInteraction> completionRequest;

    @Mock
    private SlashCommandInteraction slashEvent;

    @Mock
    private CommandAutoCompleteInteraction completionEvent;

    @Test
    @DisplayName("resolve() should resolve simple slash command")
    void resolve_shouldResolveSlash() {

        SlashRouteResolver resolver = new SlashRouteResolver();
        resolver.registerController(new TestController());

        when(this.slashRequest.getUri()).thenReturn(URI.create("slash://hello"));
        when(this.slashRequest.getEvent()).thenReturn(this.slashEvent);

        Optional<Endpoint<?>> result = resolver.resolve(this.slashRequest);

        assertTrue(result.isPresent());
        assertEquals(SlashCommandInteraction.class, result.get().interactionType());
    }

    @Test
    @DisplayName("resolve() should resolve autocomplete route")
    void resolve_shouldResolveAutocomplete() {

        SlashRouteResolver resolver = new SlashRouteResolver();
        resolver.registerController(new TestController());

        when(this.completionRequest.getUri()).thenReturn(URI.create("completion://auto/arg"));
        when(this.completionRequest.getEvent()).thenReturn(this.completionEvent);

        Optional<Endpoint<?>> result = resolver.resolve(this.completionRequest);

        assertTrue(result.isPresent());
        assertEquals(CommandAutoCompleteInteraction.class, result.get().interactionType());
    }

    @Test
    @DisplayName("getJdaCommands() should generate correct command data")
    void getJdaCommands_shouldGenerateData() {

        SlashRouteResolver resolver = new SlashRouteResolver();
        resolver.registerController(new TestController());

        Set<CommandData> commands = resolver.getJdaCommands();

        // Expecting 'hello' and 'auto'
        assertEquals(2, commands.size());

        SlashCommandData autoCmd = (SlashCommandData) commands.stream()
                                                              .filter(c -> c.getName().equals("auto"))
                                                              .findFirst().orElseThrow();

        assertTrue(autoCmd.getOptions().getFirst().isAutoComplete());
    }

    @Test
    @DisplayName("registerController() should fail when named provider is missing")
    void registerController_shouldFail_missingProvider() {

        SlashRouteResolver resolver = new SlashRouteResolver();

        // Should throw because "missing" provider isn't registered
        assertThrows(
                IllegalStateException.class, () ->
                        resolver.registerController(new MissingProviderController())
        );
    }

    @Test
    @DisplayName("registerController() should succeed with multiple options")
    void registerController_shouldRegisterOptions() {

        SlashRouteResolver resolver = new SlashRouteResolver();
        assertDoesNotThrow(() -> resolver.registerController(new OptionOrderController()));
    }

    @Test
    @DisplayName("resolve() should resolve named completion provider")
    void resolve_shouldResolveNamedProvider() {

        SlashRouteResolver resolver = new SlashRouteResolver();

        // Register the provider first
        resolver.registerCompletionProvider("myProvider", _ -> Stream.empty());
        resolver.registerController(new NamedProviderController());

        when(this.completionRequest.getUri()).thenReturn(URI.create("completion://named/opt"));
        when(this.completionRequest.getEvent()).thenReturn(this.completionEvent);

        assertTrue(resolver.resolve(this.completionRequest).isPresent());
    }

    // --- Controllers ---

    static class TestController {

        @Slash(name = "hello", description = "Say hello")
        public String hello() {return "Hello";}

        @Slash(
                name = "auto",
                description = "Autocompleted",
                options = {
                        @Option(
                                name = "arg",
                                description = "Arg",
                                type = OptionType.STRING,
                                completion = @Completion(
                                        choices = {
                                                @Choice(label = "A", value = "A")
                                        }
                                )
                        )
                })
        public String auto() {

            return "Auto";
        }

    }

    static class MissingProviderController {

        @Slash(
                name = "fail",
                description = "d",
                options = {
                        @Option(
                                name = "opt",
                                description = "d",
                                type = OptionType.STRING,
                                completion = @Completion(named = "missing")
                        )
                }
        )
        public String fail() {

            return "";
        }

    }

    static class NamedProviderController {

        @Slash(
                name = "named",
                description = "d",
                options = {
                        @Option(
                                name = "opt",
                                description = "d",
                                type = OptionType.STRING,
                                completion = @Completion(named = "myProvider")
                        )
                }
        )
        public String named() {

            return "";
        }

    }

    static class OptionOrderController {

        @Slash(
                name = "orderer",
                description = "d",
                options = {
                        @Option(
                                name = "opt1",
                                description = "d",
                                type = OptionType.STRING,
                                required = true
                        ),
                        @Option(
                                name = "opt2",
                                description = "d",
                                type = OptionType.STRING
                        )
                }
        )
        public String ordered() {

            return "";
        }

    }

}
