package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;

import java.io.IOException;

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
    public void render(TextGraphics tg, int startRow) throws IOException {
        tg.setForegroundColor(foreground);
        tg.setBackgroundColor(background);
        tg.putString(2, startRow, content);
    }

    public static class Builder {
        private String content = "";
        private TextColor foreground = TextColor.ANSI.WHITE;
        private TextColor background = TextColor.ANSI.BLACK;

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withTheme(DialogTheme theme) {
            this.foreground = theme.menuItemStyle().foreground();
            this.background = theme.menuItemStyle().background();
            return this;
        }

        public Builder withForegroundColor(TextColor color) {
            this.foreground = color;
            return this;
        }

        public Builder withBackgroundColor(TextColor color) {
            this.background = color;
            return this;
        }

        public ContentArea build() {
            return new ContentArea(this);
        }
    }
}
