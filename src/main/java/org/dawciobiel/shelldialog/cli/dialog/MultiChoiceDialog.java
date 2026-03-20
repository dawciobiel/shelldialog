package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MultiChoiceDialog extends AbstractDialog<List<DialogOption>> {
    protected MultiChoiceDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    @Override
    protected Optional<List<DialogOption>> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
