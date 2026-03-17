package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;

import java.io.IOException;

public class YesNoDialog extends AbstractDialog {
    protected YesNoDialog(String inputStreamPath, String outputStreamPath) {
        super(inputStreamPath, outputStreamPath);
    }

    @Override
    protected Value runDialog(Screen screen) throws IOException {
        return null;
    }
}
