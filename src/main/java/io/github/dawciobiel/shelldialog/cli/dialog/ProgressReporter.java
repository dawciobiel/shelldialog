package io.github.dawciobiel.shelldialog.cli.dialog;

/**
 * Interface used by background tasks to report progress to a {@link ProgressDialog}.
 */
public interface ProgressReporter {

    /**
     * Updates the current progress.
     *
     * @param progress value between 0.0 and 1.0
     * @param message  optional status message to display
     */
    void update(double progress, String message);

    /**
     * Checks if the user has requested to cancel the operation (e.g., by pressing Escape).
     *
     * @return {@code true} if cancellation was requested
     */
    boolean isCancelled();
}
