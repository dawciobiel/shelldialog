package io.github.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;

/**
 * Renders a single-line input field.
 */
public class InputArea implements Renderable {

    private final String content;
    private final TextColor foreground;
    private final TextColor background;

    private InputArea(Builder builder) {
        this.content = builder.content;
        this.foreground = builder.foreground;
        this.background = builder.background;
    }

    @Override
    public void render(TextGraphics tg, int startColumn, int startRow) throws IOException {
        tg.setForegroundColor(foreground);
        tg.setBackgroundColor(background);
        tg.putString(startColumn, startRow, content);
    }

    @Override
    public int getWidth() {
        return content.length();
    }

    @Override
    public int getHeight() {
        return 1;
    }

    /**
     * Creates a copy of this area with different content while preserving colors.
     *
     * @param content the input text to display
     * @return a new area instance with the supplied content
     */
    public InputArea withContent(String content) {
        return new Builder()
                .withContent(content)
                .withForegroundColor(foreground)
                .withBackgroundColor(background)
                .build();
    }

    /**
     * Builder for {@link InputArea} instances.
     */
    public static class Builder {
        private String content = "";
        private TextColor foreground = TextColor.ANSI.WHITE;
        private TextColor background = TextColor.ANSI.BLACK;

        /**
         * Creates an empty builder with default colors.
         */
        public Builder() {
        }

        /**
         * Sets the initial content displayed in the input field.
         *
         * @param content the input text
         * @return this builder
         */
        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        /**
         * Applies input colors from the provided theme.
         *
         * @param theme the theme supplying input colors
         * @return this builder
         */
        public Builder withTheme(DialogTheme theme) {
            this.foreground = theme.inputStyle().foreground();
            this.background = theme.inputStyle().background();
            return this;
        }

        /**
         * Sets the foreground color.
         *
         * @param color the text color
         * @return this builder
         */
        public Builder withForegroundColor(TextColor color) {
            this.foreground = color;
            return this;
        }

        /**
         * Sets the background color.
         *
         * @param color the background color
         * @return this builder
         */
        public Builder withBackgroundColor(TextColor color) {
            this.background = color;
            return this;
        }

        /**
         * Builds the input area.
         *
         * @return a new {@link InputArea}
         */
        public InputArea build() {
            return new InputArea(this);
        }
    }
}
