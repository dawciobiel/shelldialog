package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 * Represents a UI fragment that can render itself onto a Lanterna text surface.
 */
public interface Renderable {

    /**
     * Renders this fragment starting at the provided row.
     *
     * @param tg the graphics context used for drawing
     * @param startColumn the first column available for rendering
     * @param startRow the first row available for rendering
     * @throws IOException if rendering fails
     */
    void render(TextGraphics tg, int startColumn, int startRow) throws IOException;

    /**
     * Returns the width of the rendered content.
     *
     * @return the content width in terminal cells
     */
    int getWidth();

    /**
     * Returns the height of the rendered content.
     *
     * @return the content height in terminal rows
     */
    int getHeight();
}
