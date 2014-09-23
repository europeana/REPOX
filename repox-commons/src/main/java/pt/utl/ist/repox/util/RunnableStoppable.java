package pt.utl.ist.repox.util;

/**
 */
public interface RunnableStoppable extends Runnable {

    /**
     * Stop a running thread by setting a variable that is checked periodically
     * by that thread to determine if it is time to exit the execution.
     */
    public void stop();
}
