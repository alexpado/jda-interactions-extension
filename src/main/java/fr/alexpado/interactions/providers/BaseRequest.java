package fr.alexpado.interactions.providers;

import fr.alexpado.interactions.interfaces.routing.Request;
import net.dv8tion.jda.api.interactions.Interaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class BaseRequest<T extends Interaction> implements Request<T> {

    private final T                     event;
    private final URI                   uri;
    private final Map<String, Object>   parameters;
    private final Map<String, Object>   attributes;
    private final Map<Class<?>, Object> attachments;

    public BaseRequest(T event, URI uri) {

        this.event       = event;
        this.uri         = uri;
        this.parameters  = new HashMap<>();
        this.attributes  = new HashMap<>();
        this.attachments = new HashMap<>();
    }

    @NotNull
    @Override
    public T getEvent() {

        return this.event;
    }

    @NotNull
    @Override
    public URI getUri() {

        return this.uri;
    }

    @NotNull
    @Override
    public Map<String, Object> getParameters() {

        return this.parameters;
    }

    @NotNull
    @Override
    public Map<String, Object> getAttributes() {

        return this.attributes;
    }

    @Override
    public <A> void addAttachment(@NotNull Class<A> attachmentClass, @NotNull A attachment) {

        this.attachments.put(attachmentClass, attachment);
    }

    @Override
    public <A> @Nullable A getAttachment(Class<A> attachmentClass) {

        Object attachement = this.attachments.get(attachmentClass);

        if (attachement != null) {

            if (attachmentClass.isAssignableFrom(attachement.getClass())) {
                return (A) attachement;
            }

            throw new ClassCastException(String.format(
                    "Unable to retrieve attachment of type '%s': The attachment retrieve is of incompatible type of '%s'",
                    attachmentClass.getName(),
                    attachement.getClass()
            ));
        }

        return null;
    }

}
