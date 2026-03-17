package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.header.border.BorderLine;

import java.io.IOException;

public class Header implements Renderable {

    private final String title;
    private final int width;
    private final TextColor borderColor;
    private final TextColor titleColor;

    public Header(String title, int width, TextColor borderColor, TextColor titleColor) {
        this.title = title;
        this.width = width;
        this.borderColor = borderColor;
        this.titleColor = titleColor;
    }

    @Override
    public void render(TextGraphics tg, int startRow) throws IOException {
        tg.setForegroundColor(borderColor);
        tg.putString(0, startRow++, BorderLine.DOUBLE_TOP_LEFT + BorderLine.DOUBLE_HORIZONTAL.repeat(width - 2) + BorderLine.DOUBLE_TOP_RIGHT);

        tg.setForegroundColor(titleColor);
        // @formatter:off
        tg.putString(0, startRow++,
                BorderLine.DOUBLE_VERTICAL
                        + BorderLine.NO.repeat(2)
                        + title
                        + BorderLine.NO.repeat(width - 2 - title.length() - 2 )
                        + BorderLine.DOUBLE_VERTICAL);
        // @formatter:on

        tg.setForegroundColor(borderColor);
        tg.putString(0, startRow++, BorderLine.DOUBLE_BOTTOM_LEFT + BorderLine.DOUBLE_HORIZONTAL.repeat(width - 2) + BorderLine.DOUBLE_BOTTOM_RIGHT);
    }
}