package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Base class for dialogs rendered on a Lanterna screen.
 * Supports multiple input/output sources including direct Terminal instances for testing.
 *
 * @param <T> the type returned when the dialog is accepted
 */
public abstract class AbstractDialog<T> implements Showable<T> {

    private final String inputStreamPath;
    private final String outputStreamPath;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Terminal terminal;

    /**
     * Creates the dialog base with all possible I/O configurations.
     *
     * @param in       the input stream (optional, for testing)
     * @param out      the output stream (optional, for testing)
     * @param inPath   the input device path (default /dev/tty)
     * @param outPath  the output device path (default /dev/tty)
     * @param terminal direct terminal instance (optional, for advanced testing)
     */
    protected AbstractDialog(InputStream in, OutputStream out, String inPath, String outPath, Terminal terminal) {
        this.inputStream = in;
        this.outputStream = out;
        this.inputStreamPath = inPath;
        this.outputStreamPath = outPath;
        this.terminal = terminal;
    }

    /**
     * Displays the dialog to the user and waits for input.
     * Selects the appropriate I/O source based on provided configuration.
     *
     * @return An {@link Optional} containing the result of the interaction if successful,
     *         or {@link Optional#empty()} if canceled or an error occurred.
     */
    @Override
    public Optional<T> show() {
        if (terminal != null) {
            try (Screen screen = new TerminalScreen(terminal)) {
                screen.startScreen();
                return runDialog(screen);
            } catch (IOException e) {
                return Optional.empty();
            }
        }

        if (inputStream != null && outputStream != null) {
            return showWithStreams(inputStream, outputStream);
        }

        try (FileInputStream ttyInput = new FileInputStream(inputStreamPath);
             FileOutputStream ttyOutput = new FileOutputStream(outputStreamPath)) {
            return showWithStreams(ttyInput, ttyOutput);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<T> showWithStreams(InputStream in, OutputStream out) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory(out, in, StandardCharsets.UTF_8);
        factory.setForceTextTerminal(true);

        try (Screen screen = factory.createScreen()) {
            screen.startScreen();
            return runDialog(screen);
        } catch (IOException e) {
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
