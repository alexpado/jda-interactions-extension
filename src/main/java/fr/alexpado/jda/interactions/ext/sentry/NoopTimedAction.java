package fr.alexpado.jda.interactions.ext.sentry;

/**
 * Implementation of {@link ITimedAction} when Sentry is disabled.
 */
public class NoopTimedAction implements ITimedAction {

    /**
     * Start the timing transaction of the current {@link ITimedAction}.
     *
     * @param name
     *         The name of this timing transaction.
     * @param description
     *         The description of this timing transaction.
     */
    @Override
    public void open(String name, String description) {

    }

    /**
     * Start an action, opening a sub-transaction withing the actual timing. Calling this multiple time will open
     * another sub-transaction (inside the current sub-transaction). This can be repeated an unlimited amount of time.
     *
     * @param name
     *         The name of the action that will be executed.
     * @param description
     *         The description of the action that will be executed.
     */
    @Override
    public void action(String name, String description) {

    }

    /**
     * Stop the last opened action, causing its transaction to be closed.
     */
    @Override
    public void endAction() {

    }

    /**
     * Close all opened transaction and send the timing information to Sentry.
     */
    @Override
    public void close() {

    }


}
