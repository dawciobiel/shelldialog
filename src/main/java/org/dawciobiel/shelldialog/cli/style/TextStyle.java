package org.dawciobiel.shelldialog.cli.style;

import com.googlecode.lanterna.TextColor;

import java.util.Objects;

/**
 * A class that stores the text and background color.
 */
public final class TextStyle {

    private final TextColor foreground;
    private final TextColor background;

    private TextStyle(TextColor foreground, TextColor background) {
        this.foreground = Objects.requireNonNull(foreground);
        this.background = Objects.requireNonNull(background);
    }

    public TextColor foreground() {
        return foreground;
    }

    public TextColor background() {
        return background;
    }

    public static TextStyle of(TextColor foreground, TextColor background) {
        return new TextStyle(foreground, background);
    }

    public static TextStyle ofAnsi(TextColor foreground, TextColor background) {
        return new TextStyle(foreground, background);
    }
}