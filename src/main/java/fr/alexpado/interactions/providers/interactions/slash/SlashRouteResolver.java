package fr.alexpado.interactions.providers.interactions.slash;

import fr.alexpado.interactions.annotations.Completion;
import fr.alexpado.interactions.annotations.Option;
import fr.alexpado.interactions.annotations.Slash;
import fr.alexpado.interactions.interfaces.routing.DeferrableRoute;
import fr.alexpado.interactions.interfaces.routing.Request;
import fr.alexpado.interactions.interfaces.routing.Route;
import fr.alexpado.interactions.interfaces.routing.RouteResolver;
import fr.alexpado.interactions.providers.ReflectiveRouteHandler;
import fr.alexpado.interactions.providers.interactions.slash.handlers.CompletionRouteHandler;
import fr.alexpado.interactions.providers.interactions.slash.interfaces.CompletionProvider;
import fr.alexpado.interactions.structure.Endpoint;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A self-contained {@link RouteResolver} that manages Slash Commands and their associated Autocomplete logic.
 * <p>
 * This class maintains its own routing table for {@code slash://} and {@code completion://} schemes.
 */
public class SlashRouteResolver implements RouteResolver {

    private final Map<URI, Endpoint<SlashCommandInteraction>>        slashEndpoints      = new HashMap<>();
    private final Map<URI, Endpoint<CommandAutoCompleteInteraction>> completionEndpoints = new HashMap<>();
    private final Map<String, CompletionProvider>                    completionProviders = new HashMap<>();
    private final Set<CommandData>                                   commands            = new HashSet<>();

    public Set<CommandData> getJdaCommands() {

        return UnmodifiableSet.unmodifiableSet(this.commands);
    }

    /**
     * Registers a controller object. Scans for {@link Slash} annotations.
     *
     * @param controller
     *         The object to scan.
     */
    public void registerController(Object controller) {

        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Slash.class)) {
                this.registerSlashMethod(controller, method);
            }
        }
    }

    /**
     * Registers a shared completion provider that can be referenced by name in {@link Completion#named()}.
     *
     * @param name
     *         The unique name of the provider.
     * @param provider
     *         The completion provider.
     */
    public void registerCompletionProvider(String name, CompletionProvider provider) {

        this.completionProviders.put(name, provider);
    }

    @Override
    public <T extends Interaction> Optional<Endpoint<?>> resolve(Request<T> request) {

        URI    uri    = Request.normalize(request.getUri());
        String scheme = uri.getScheme();

        if ("slash".equals(scheme) && request.getEvent() instanceof SlashCommandInteraction) {
            return Optional.ofNullable(this.slashEndpoints.get(uri));
        }

        if ("completion".equals(scheme) && request.getEvent() instanceof CommandAutoCompleteInteraction) {
            return Optional.ofNullable(this.completionEndpoints.get(uri));
        }

        return Optional.empty();
    }

    private void registerSlashMethod(Object controller, Method method) {

        Slash  slash = method.getAnnotation(Slash.class);
        String path  = slash.name();

        URI uri = URI.create("slash://" + path);

        Endpoint<SlashCommandInteraction> endpoint = new Endpoint<>(
                DeferrableRoute.of(uri, method),
                new ReflectiveRouteHandler<>(controller, method),
                SlashCommandInteraction.class
        );

        this.slashEndpoints.put(uri, endpoint);

        for (Option option : slash.options()) {
            if (this.isAutocomplete(option)) {
                this.registerCompletionRoute(path, option);
            }
        }

        this.addCommandDefinition(slash);
    }

    private void registerCompletionRoute(String commandPath, Option option) {

        URI completionUri = URI.create("completion://" + commandPath + "/" + option.name());

        Completion completion = option.completion();
        Route      route      = () -> completionUri;

        CompletionProvider provider = null;

        if (completion.choices().length > 0) {
            provider = (_) -> Stream.of(completion.choices()).map(choice -> new Command.Choice(choice.label(), choice.value()));
        }

        if (!completion.named().isBlank()) {
            String             providerName       = completion.named().trim();
            CompletionProvider completionProvider = this.completionProviders.get(providerName);

            if (completionProvider == null) {
                throw new IllegalStateException(String.format(
                        "Could not complete: Option '%s' for command '%s' references completion provider of name '%s' but such provider was not registered.",
                        option.name(),
                        commandPath,
                        providerName
                ));
            }

            provider = completionProvider;
        }

        if (provider == null) {
            throw new IllegalStateException(String.format(
                    "Could not complete: Option '%s' for command '%s' has no completion candidate.",
                    option.name(),
                    commandPath
            ));
        }

        this.completionEndpoints.put(
                completionUri,
                new Endpoint<>(route, new CompletionRouteHandler(provider), CommandAutoCompleteInteraction.class)
        );
    }

    //region JDA Stuff

    private void addCommandDefinition(Slash slash) {

        String[] parts = slash.name().split("/");

        // 1. Root Command
        SlashCommandData root = (SlashCommandData) this.commands
                .stream()
                .filter(c -> c.getName().equals(parts[0]))
                .findFirst()
                .orElseGet(() -> {
                    SlashCommandData d = Commands.slash(parts[0], parts.length == 1 ? slash.description() : "Group");
                    this.commands.add(d);
                    return d;
                });

        if (parts.length == 1) {
            root.setDescription(slash.description());
            this.applyOptions(root::addOptions, slash.options());
        } else if (parts.length == 2) {
            SubcommandData sub = new SubcommandData(parts[1], slash.description());
            this.applyOptions(sub::addOptions, slash.options());
            root.addSubcommands(sub);
        } else if (parts.length == 3) {
            SubcommandGroupData group = root.getSubcommandGroups().stream()
                                            .filter(g -> g.getName().equals(parts[1]))
                                            .findFirst()
                                            .orElseGet(() -> {
                                                SubcommandGroupData g = new SubcommandGroupData(parts[1], "Group");
                                                root.addSubcommandGroups(g);
                                                return g;
                                            });

            SubcommandData sub = new SubcommandData(parts[2], slash.description());
            this.applyOptions(sub::addOptions, slash.options());
            group.addSubcommands(sub);
        }
    }

    private void applyOptions(Consumer<OptionData> consumer, Option[] options) {

        Stream.of(options)
              .sorted(Comparator.comparing(Option::required))
              .map(this::createOptionData)
              .forEach(consumer);
    }

    private OptionData createOptionData(Option opt) {

        OptionData data = new OptionData(
                opt.type(),
                opt.name(),
                opt.description(),
                opt.required(),
                this.isAutocomplete(opt)
        );

        if (opt.type() == OptionType.INTEGER) {
            if (opt.minInt() != Long.MIN_VALUE) data.setMinValue(opt.minInt());
            if (opt.maxInt() != Long.MAX_VALUE) data.setMaxValue(opt.maxInt());
        } else if (opt.type() == OptionType.NUMBER) {
            if (opt.minNum() != Double.NEGATIVE_INFINITY) data.setMinValue(opt.minNum());
            if (opt.maxNum() != Double.POSITIVE_INFINITY) data.setMaxValue(opt.maxNum());
        } else if (opt.type() == OptionType.STRING) {
            if (opt.minLength() != -1) data.setMinLength(opt.minLength());
            if (opt.maxLength() != -1) data.setMaxLength(opt.maxLength());
        }

        return data;
    }

    private boolean isAutocomplete(Option opt) {

        return !opt.completion().named().isEmpty() || opt.completion().choices().length > 0;
    }

    //endregion

}
