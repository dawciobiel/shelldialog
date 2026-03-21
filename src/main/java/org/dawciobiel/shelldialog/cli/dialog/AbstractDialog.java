package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Base class for dialogs rendered on a Lanterna screen.
 *
 * @param <T> the type returned when the dialog is accepted
 */
public abstract class AbstractDialog<T> implements Showable<T> {

    private final String inputStreamPath;
    private final String outputStreamPath;

    /**
     * Creates the dialog base with the provided terminal device paths.
     *
     * @param inputStreamPath the path used for terminal input
     * @param outputStreamPath the path used for terminal output
     */
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

    /**
     * Executes the dialog-specific interaction using an already initialized screen.
     *
     * @param screen the active Lanterna screen
     * @return an optional result produced by the dialog
     * @throws IOException if screen I/O fails during the interaction
     */
    protected abstract Optional<T> runDialog(Screen screen) throws IOException;
}
