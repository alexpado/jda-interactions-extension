package fr.alexpado.jda.interactions.interfaces;

import fr.alexpado.jda.interactions.interfaces.interactions.InteractionErrorHandler;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionExecutor;
import fr.alexpado.jda.interactions.interfaces.interactions.InteractionResponseHandler;

public interface FeatureContainer extends ExecutableItem, InteractionExecutor, InteractionErrorHandler, InteractionResponseHandler {
}
