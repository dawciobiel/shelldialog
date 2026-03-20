package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.Optional;

public class PasswordDialog extends AbstractDialog<char[]> {
    protected PasswordDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    @Override
    protected Optional<char[]> runDialog(Screen screen) throws IOException {
        return Optional.empty();
    }
}
