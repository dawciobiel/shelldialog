package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class AbstractDialog implements Showable {

    private final String inputStreamPath;
    private final String outputStreamPath;

    protected AbstractDialog(String inputStreamPath, String outputStreamPath) {
        this.inputStreamPath = inputStreamPath;
        this.outputStreamPath = outputStreamPath;
    }

    /**
     * Displays the dialog to the user and waits for input.
     * This method sets up the Lanterna screen and handles I/O exceptions.
     * The actual dialog logic is implemented in {@link #runDialog(Screen)}.
     *
     * @return A {@link Value} representing the result of the interaction.
     *         The specific type of Value depends on the concrete dialog implementation.
     *         Returns {@link ErrorValue} if an I/O error occurs.
     */
    @Override
    public Value show() {

        try (FileInputStream ttyInput = new FileInputStream(inputStreamPath);
             FileOutputStream ttyOutput = new FileOutputStream(outputStreamPath)) {

            DefaultTerminalFactory factory =
                    new DefaultTerminalFactory(ttyOutput, ttyInput, StandardCharsets.UTF_8);

            factory.setForceTextTerminal(true);

            try (Screen screen = factory.createScreen()) {
                screen.startScreen();
                return runDialog(screen);
            }

        } catch (IOException e) {
            return new ErrorValue(e.getLocalizedMessage());
        }
    }

    protected abstract Value runDialog(Screen screen) throws IOException;
}