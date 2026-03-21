package org.dawciobiel.shelldialog.cli.dialog;

import java.util.Optional;

/**
 * Defines a dialog that can be presented to the user and yield a result.
 *
 * @param <T> the type produced when the dialog is accepted
 */
public interface Showable<T> {

    /**
     * Shows the dialog and waits until it is accepted, canceled, or fails.
     *
     * @return an {@link Optional} containing the result, or an empty optional when the dialog does not complete successfully
     */
    Optional<T> show();
}
