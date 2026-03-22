package io.github.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;

/**
 * Renders a single line of static dialog content.
 */
public class ContentArea implements Renderable {

    private final String content;
    private final TextColor foreground;
    private final TextColor background;

    private ContentArea(Builder builder) {
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

    /**
     * Returns the number of rows occupied by this area.
     *
     * @return the rendered height in rows
     */
    public int getHeight() {
        return 1;
    }

    @Override
    public int getWidth() {
        return content.length();
    }

    /**
     * Creates a copy of this area with different content while preserving colors.
     *
     * @param content the text to render
     * @return a new area instance with the supplied content
     */
    public ContentArea withContent(String content) {
        return new Builder()
                .withContent(content)
                .withForegroundColor(foreground)
                .withBackgroundColor(background)
                .build();
    }

    /**
     * Builder for {@link ContentArea} instances.
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
         * Sets the content to render.
         *
         * @param content the text content
         * @return this builder
         */
        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        /**
         * Applies content colors from the provided theme.
         *
         * @param theme the theme supplying content colors
         * @return this builder
         */
        public Builder withTheme(DialogTheme theme) {
            this.foreground = theme.contentStyle().foreground();
            this.background = theme.contentStyle().background();
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
         * Builds the content area.
         *
         * @return a new {@link ContentArea}
         */
        public ContentArea build() {
            return new ContentArea(this);
        }
    }
}
