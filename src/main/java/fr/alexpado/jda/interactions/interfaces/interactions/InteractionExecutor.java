package fr.alexpado.jda.interactions.interfaces.interactions;

import fr.alexpado.jda.interactions.entities.DispatchEvent;
import fr.alexpado.jda.interactions.interfaces.ExecutableItem;

import java.net.URI;
import java.util.Optional;

public interface InteractionExecutor {

    boolean canResolve(URI uri);

    Optional<ExecutableItem> resolve(URI path);

    void prepare(DispatchEvent event);

}
