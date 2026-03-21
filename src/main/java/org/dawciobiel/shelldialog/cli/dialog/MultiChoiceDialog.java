package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Placeholder for a dialog that will allow selecting multiple options from a list.
 */
public class MultiChoiceDialog extends AbstractDialog<List<DialogOption>> {

    /**
     * Creates the dialog with the provided terminal device paths.
     *
     * @param inputStreamPath the path used for terminal input
     * @param outputStreamPath the path used for terminal output
     */
    protected MultiChoiceDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<List<DialogOption>> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
