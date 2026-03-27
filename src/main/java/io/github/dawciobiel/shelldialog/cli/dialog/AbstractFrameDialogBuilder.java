package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Shared builder support for dialogs that expose the same frame configuration API.
 *
 * @param <T> the concrete builder type
 */
public abstract class AbstractFrameDialogBuilder<T extends AbstractFrameDialogBuilder<T>> {

    /**
     * Controls whether the shared frame around the dialog is rendered.
     */
    protected boolean borderVisible = true;

    /**
     * The visual style used when the shared dialog frame is rendered.
     */
    protected TextStyle borderStyle = TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT);

    protected String inputStreamPath = "/dev/tty";
    protected String outputStreamPath = "/dev/tty";
    protected InputStream inputStream;
    protected OutputStream outputStream;
    protected Terminal terminal;

    /**
     * Enables or disables the shared dialog border.
     *
     * @param visible {@code true} to render the border, {@code false} to omit it
     * @return this builder
     */
    public T withBorder(boolean visible) {
        this.borderVisible = visible;
        return self();
    }

    /**
     * Sets the style used for the shared dialog border.
     *
     * @param style the border style
     * @return this builder
     */
    public T withBorderStyle(TextStyle style) {
        this.borderStyle = Objects.requireNonNull(style);
        return self();
    }

    /**
     * Sets the foreground color used for the shared dialog border.
     *
     * @param color the border foreground color
     * @return this builder
     */
    public T withBorderColor(TextColor color) {
        this.borderStyle = TextStyle.of(Objects.requireNonNull(color), borderStyle.background());
        return self();
    }

    /**
     * Applies the dialog border style from the provided theme.
     *
     * @param theme the theme supplying the border style
     * @return this builder
     */
    public T withTheme(DialogTheme theme) {
        this.borderStyle = Objects.requireNonNull(theme).borderStyle();
        return self();
    }

    /**
     * Sets the input stream path (e.g., "/dev/tty").
     *
     * @param path The path to the input stream.
     * @return This Builder instance.
     */
    public T inputStream(String path) {
        this.inputStreamPath = Objects.requireNonNull(path);
        return self();
    }

    /**
     * Sets the output stream path (e.g., "/dev/tty").
     *
     * @param path The path to the output stream.
     * @return This Builder instance.
     */
    public T outputStream(String path) {
        this.outputStreamPath = Objects.requireNonNull(path);
        return self();
    }

    /**
     * Sets the input and output streams directly (useful for testing).
     *
     * @param in  the input stream
     * @param out the output stream
     * @return this builder
     */
    public T withStreams(InputStream in, OutputStream out) {
        this.inputStream = Objects.requireNonNull(in);
        this.outputStream = Objects.requireNonNull(out);
        return self();
    }

    /**
     * Sets a custom terminal instance (useful for testing).
     *
     * @param terminal the terminal instance
     * @return this builder
     */
    public T withTerminal(Terminal terminal) {
        this.terminal = Objects.requireNonNull(terminal);
        return self();
    }

    /**
     * Returns the concrete builder type for fluent chaining in subclasses.
     *
     * @return this builder cast to its concrete type
     */
    protected abstract T self();
}
