package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Optional;

/**
 * Placeholder for a dialog that will capture a password as a character array.
 */
public class PasswordDialog extends AbstractDialog<char[]> {

    /**
     * Creates the dialog with the provided terminal device paths.
     *
     * @param inputStreamPath the path used for terminal input
     * @param outputStreamPath the path used for terminal output
     */
    protected PasswordDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<char[]> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
