package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Optional;

public class YesNoDialog extends AbstractDialog<Boolean> {
    protected YesNoDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    @Override
    protected Optional<Boolean> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
