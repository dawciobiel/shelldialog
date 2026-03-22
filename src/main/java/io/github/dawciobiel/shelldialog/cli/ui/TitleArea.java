package io.github.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Renders one or more title lines at the top of a dialog.
 */
public class TitleArea implements Renderable {

    private final List<String> titleLines;
    private final TextColor titleColor;

    private TitleArea(Builder builder) {
        this.titleLines = new ArrayList<>(builder.titleLines);
        this.titleColor = builder.titleColor;
    }

    @Override
    public void render(TextGraphics tg, int startColumn, int startRow) throws IOException {
        tg.setForegroundColor(titleColor);
        for (int i = 0; i < titleLines.size(); i++) {
            tg.putString(startColumn, startRow + i, titleLines.get(i));
        }
    }

    /**
     * Returns the number of lines this title area occupies.
     * @return The height of the title area.
     */
    public int getHeight() {
        return titleLines.size();
    }

    @Override
    public int getWidth() {
        return titleLines.stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    /**
     * Builder for {@link TitleArea} instances.
     */
    public static class Builder {
        private final List<String> titleLines = new ArrayList<>();
        private TextColor titleColor = TextColor.ANSI.WHITE;

        /**
         * Creates an empty builder with default colors.
         */
        public Builder() {
        }

        /**
         * Appends title lines.
         *
         * @param lines title lines to append; entries containing newline characters are split into separate lines
         * @return this builder
         */
        public Builder withTitle(String... lines) {
            if (lines != null) {
                for (String line : lines) {
                    if (line != null) {
                        // Handle potential newline characters within a single string
                        if (line.contains("\n")) {
                            Collections.addAll(this.titleLines, line.split("\n"));
                        } else {
                            this.titleLines.add(line);
                        }
                    }
                }
            }
            return this;
        }

        /**
         * Appends title lines from a list.
         *
         * @param lines title lines to append
         * @return this builder
         */
        public Builder withTitle(List<String> lines) {
            if (lines != null) {
                this.titleLines.addAll(lines);
            }
            return this;
        }

        /**
         * Applies title colors from the provided theme.
         *
         * @param theme the theme supplying title colors
         * @return this builder
         */
        public Builder withTheme(DialogTheme theme) {
            this.titleColor = theme.titleStyle().foreground();
            return this;
        }

        /**
         * Sets the title text color.
         *
         * @param color the title color
         * @return this builder
         */
        public Builder withTitleColor(TextColor color) {
            this.titleColor = color;
            return this;
        }

        /**
         * Builds the title area.
         *
         * @return a new {@link TitleArea}
         */
        public TitleArea build() {
            return new TitleArea(this);
        }
    }
}
