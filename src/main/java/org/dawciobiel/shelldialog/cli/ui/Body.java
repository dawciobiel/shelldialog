package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

public class Body implements Renderable {

    private final String content;
    private final TextColor foreground;
    private final TextColor background;

    public Body(String content, TextColor foreground, TextColor background) {
        this.content = content;
        this.foreground = foreground;
        this.background = background;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        tg.setForegroundColor(foreground);
        tg.setBackgroundColor(background);
        tg.putString(2, startRow, content);
    }
}