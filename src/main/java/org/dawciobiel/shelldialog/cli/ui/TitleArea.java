package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TitleArea implements Renderable {

    private final List<String> titleLines;
    private final TextColor borderColor;
    private final TextColor titleColor;

    private TitleArea(Builder builder) {
        this.titleLines = new ArrayList<>(builder.titleLines);
        this.borderColor = builder.borderColor;
        this.titleColor = builder.titleColor;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        tg.setForegroundColor(titleColor);
        for (int i = 0; i < titleLines.size(); i++) {
            tg.putString(2, startRow + i, titleLines.get(i));
        }
    }

    /**
     * Returns the number of lines this title area occupies.
     * @return The height of the title area.
     */
    public int getHeight() {
        return titleLines.size();
    }

    public static class Builder {
        private final List<String> titleLines = new ArrayList<>();
        private TextColor borderColor = TextColor.ANSI.WHITE;
        private TextColor titleColor = TextColor.ANSI.WHITE;

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
        
        public Builder withTitle(List<String> lines) {
            if (lines != null) {
                this.titleLines.addAll(lines);
            }
            return this;
        }

        public Builder withTheme(DialogTheme theme) {
            this.borderColor = theme.borderStyle().foreground();
            this.titleColor = theme.titleStyle().foreground();
            return this;
        }
        
        public Builder withBorderColor(TextColor color) {
            this.borderColor = color;
            return this;
        }

        public Builder withTitleColor(TextColor color) {
            this.titleColor = color;
            return this;
        }

        public TitleArea build() {
            return new TitleArea(this);
        }
    }
}
