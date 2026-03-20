package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class AbstractDialog<T> implements Showable<T> {

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
     * @return An {@link Optional} containing the result of the interaction if successful,
     *         or {@link Optional#empty()} if canceled or an error occurred.
     */
    @Override
    public Optional<T> show() {

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
            // In a real application, you might want to log this error or rethrow a custom runtime exception
            // For now, returning empty to signal failure/cancellation as requested by the architecture change
            return Optional.empty();
        }
    }

    protected abstract Optional<T> runDialog(Screen screen) throws IOException;
}