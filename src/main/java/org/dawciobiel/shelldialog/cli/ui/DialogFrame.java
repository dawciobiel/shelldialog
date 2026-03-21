package org.dawciobiel.shelldialog.cli.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import org.dawciobiel.shelldialog.cli.style.BorderLine;
import org.dawciobiel.shelldialog.cli.style.TextStyle;

/**
 * Draws a shared frame around the whole dialog and exposes the content viewport inside it.
 */
public final class DialogFrame {

    private final boolean borderVisible;
    private final TextStyle borderStyle;
    private final int horizontalPadding;
    private final int verticalPadding;

    /**
     * Creates a frame with one-cell horizontal padding and no vertical padding.
     *
     * @param borderVisible whether the dialog frame should be drawn
     * @param borderStyle the style used for border characters
     */
    public DialogFrame(boolean borderVisible, TextStyle borderStyle) {
        this(borderVisible, borderStyle, 1, 0);
    }

    /**
     * Creates a frame with explicit content padding.
     *
     * @param borderVisible whether the dialog frame should be drawn
     * @param borderStyle the style used for border characters
     * @param horizontalPadding left/right content padding inside the frame
     * @param verticalPadding top/bottom content padding inside the frame
     */
    public DialogFrame(boolean borderVisible, TextStyle borderStyle, int horizontalPadding, int verticalPadding) {
        this.borderVisible = borderVisible;
        this.borderStyle = borderStyle;
        this.horizontalPadding = Math.max(0, horizontalPadding);
        this.verticalPadding = Math.max(0, verticalPadding);
    }

    /**
     * Calculates the outer frame dimensions and the content origin for the supplied content box.
     *
     * @param contentWidth the width required by dialog content
     * @param contentHeight the height required by dialog content
     * @return the computed frame layout
     */
    public FrameLayout layoutFor(int contentWidth, int contentHeight) {
        int borderInsetX = borderVisible ? 1 : 0;
        int borderInsetY = borderVisible ? 1 : 0;
        int outerWidth = contentWidth + (horizontalPadding * 2) + (borderInsetX * 2);
        int outerHeight = contentHeight + (verticalPadding * 2) + (borderInsetY * 2);
        int contentColumn = borderInsetX + horizontalPadding;
        int contentRow = borderInsetY + verticalPadding;
        return new FrameLayout(outerWidth, outerHeight, contentColumn, contentRow, contentWidth, contentHeight);
    }

    /**
     * Draws the frame around the provided content box.
     *
     * @param tg the graphics context used for drawing
     * @param layout the layout previously computed for the dialog content
     */
    public void render(TextGraphics tg, FrameLayout layout) {
        if (!borderVisible) {
            return;
        }

        tg.setForegroundColor(borderStyle.foreground());
        tg.setBackgroundColor(borderStyle.background());

        tg.putString(0, 0, BorderLine.SINGLE_TOP_LEFT);
        tg.putString(layout.outerWidth() - 1, 0, BorderLine.SINGLE_TOP_RIGHT);
        for (int column = 1; column < layout.outerWidth() - 1; column++) {
            tg.putString(column, 0, BorderLine.SINGLE_HORIZONTAL);
        }

        int bottomRow = layout.outerHeight() - 1;
        tg.putString(0, bottomRow, BorderLine.SINGLE_BOTTOM_LEFT);
        tg.putString(layout.outerWidth() - 1, bottomRow, BorderLine.SINGLE_BOTTOM_RIGHT);
        for (int column = 1; column < layout.outerWidth() - 1; column++) {
            tg.putString(column, bottomRow, BorderLine.SINGLE_HORIZONTAL);
        }

        for (int row = 1; row < layout.outerHeight() - 1; row++) {
            tg.putString(0, row, BorderLine.SINGLE_VERTICAL);
            tg.putString(layout.outerWidth() - 1, row, BorderLine.SINGLE_VERTICAL);
        }
    }

    /**
     * Immutable frame metrics returned by {@link #layoutFor(int, int)}.
     *
     * @param outerWidth the full width including borders and padding
     * @param outerHeight the full height including borders and padding
     * @param contentColumn the first column available for content rendering
     * @param contentRow the first row available for content rendering
     * @param contentWidth the requested content width
     * @param contentHeight the requested content height
     */
    public record FrameLayout(
            int outerWidth,
            int outerHeight,
            int contentColumn,
            int contentRow,
            int contentWidth,
            int contentHeight
    ) {
    }
}
