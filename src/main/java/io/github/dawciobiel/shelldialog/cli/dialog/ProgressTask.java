package io.github.dawciobiel.shelldialog.cli.dialog;

/**
 * Functional interface representing a task that can report its progress.
 */
@FunctionalInterface
public interface ProgressTask {

    /**
     * Executes the task.
     *
     * @param reporter the reporter used to send progress updates back to the UI
     * @throws Exception if any error occurs during execution
     */
    void run(ProgressReporter reporter) throws Exception;
}
