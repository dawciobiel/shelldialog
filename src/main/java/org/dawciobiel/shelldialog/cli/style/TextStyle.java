package org.dawciobiel.shelldialog.cli.style;

import com.googlecode.lanterna.TextColor;

import java.util.Objects;

/**
 * Immutable pair of foreground and background colors.
 */
public final class TextStyle {

    private final TextColor foreground;
    private final TextColor background;

    private TextStyle(TextColor foreground, TextColor background) {
        this.foreground = Objects.requireNonNull(foreground);
        this.background = Objects.requireNonNull(background);
    }

    /**
     * Returns the foreground color.
     *
     * @return the foreground color
     */
    public TextColor foreground() {
        return foreground;
    }

    /**
     * Returns the background color.
     *
     * @return the background color
     */
    public TextColor background() {
        return background;
    }

    /**
     * Creates a text style from the supplied colors.
     *
     * @param foreground the foreground color
     * @param background the background color
     * @return a new text style
     */
    public static TextStyle of(TextColor foreground, TextColor background) {
        return new TextStyle(foreground, background);
    }

    /**
     * Creates a text style from ANSI-compatible colors.
     *
     * @param foreground the foreground color
     * @param background the background color
     * @return a new text style
     */
    public static TextStyle ofAnsi(TextColor foreground, TextColor background) {
        return new TextStyle(foreground, background);
    }
}
