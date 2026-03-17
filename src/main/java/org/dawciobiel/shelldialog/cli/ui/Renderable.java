package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

public interface Renderable {
    void render(TextGraphics tg, int startRow) throws IOException;
}