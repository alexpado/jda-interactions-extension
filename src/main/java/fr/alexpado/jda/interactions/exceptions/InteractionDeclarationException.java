package fr.alexpado.jda.interactions.exceptions;

import java.lang.reflect.Method;

public class InteractionDeclarationException extends RuntimeException {

    private final Class<?> declarationClass;
    private final Method   declarationMethod;
    private final String   path;
    private final String   error;

    /**
     * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not initialized, and
     * may subsequently be initialized by a call to {@link #initCause}.
     */
    public InteractionDeclarationException(Class<?> declarationClass, Method declarationMethod, String path, String error) {

        this.declarationClass  = declarationClass;
        this.declarationMethod = declarationMethod;
        this.path              = path;
        this.error             = error;
    }

    /**
     * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not initialized, and
     * may subsequently be initialized by a call to {@link #initCause}.
     */
    public InteractionDeclarationException(Exception e, Class<?> declarationClass, Method declarationMethod, String path, String error) {

        super(e);
        this.declarationClass  = declarationClass;
        this.declarationMethod = declarationMethod;
        this.path              = path;
        this.error             = error;
    }

    public Class<?> getDeclarationClass() {

        return declarationClass;
    }

    public Method getDeclarationMethod() {

        return declarationMethod;
    }

    public String getPath() {

        return path;
    }

    public String getError() {

        return error;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance (which may be {@code null}).
     */
    @Override
    public String getMessage() {

        return String.format("[%s] (%s::%s) %s", this.getPath(), this.getDeclarationClass().getSimpleName(), this
                .getDeclarationMethod().getName(), this.getError());
    }

}
