package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Optional;

/**
 * Placeholder for a dialog that will return a boolean yes-or-no answer.
 */
public class YesNoDialog extends AbstractDialog<Boolean> {

    /**
     * Creates the dialog with the provided terminal device paths.
     *
     * @param inputStreamPath the path used for terminal input
     * @param outputStreamPath the path used for terminal output
     */
    protected YesNoDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<Boolean> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
