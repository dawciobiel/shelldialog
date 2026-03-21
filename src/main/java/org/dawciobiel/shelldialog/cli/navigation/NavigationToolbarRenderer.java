package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.List;

/**
 * Renders {@link NavigationToolbar} instances using configurable colors.
 */
public final class NavigationToolbarRenderer {

    private final TextColor hotkeyColor;
    private final TextColor labelColor;
    private final TextColor backgroundColor;

    /**
     * Creates a renderer with the provided colors.
     *
     * @param hotkeyColor the color used for hotkeys
     * @param labelColor the color used for action labels
     * @param backgroundColor the background color applied while rendering
     */
    public NavigationToolbarRenderer(TextColor hotkeyColor, TextColor labelColor, TextColor backgroundColor) {
        this.hotkeyColor = hotkeyColor;
        this.labelColor = labelColor;
        this.backgroundColor = backgroundColor;
    }

    /**
     * Renders the toolbar on the provided row.
     *
     * @param tg the graphics context used for drawing
     * @param toolbar the toolbar to render
     * @param row the row where rendering starts
     */
    public void render(TextGraphics tg, NavigationToolbar toolbar, int row) {
        int col = 0;
        List<NavigationItem> items = toolbar.getItems();
        String spacer = toolbar.getHotkeyLabelSeparator();
        String separator = toolbar.getItemSeparator();

        for (int i = 0; i < items.size(); i++) {
            NavigationItem item = items.get(i);

            tg.setForegroundColor(hotkeyColor);
            tg.setBackgroundColor(backgroundColor);
            tg.putString(col, row, item.hotkey());
            col += item.hotkey().length();

            tg.putString(col, row, spacer);
            col += spacer.length();

            tg.setForegroundColor(labelColor);
            tg.setBackgroundColor(backgroundColor);
            tg.putString(col, row, item.label());
            col += item.label().length();

            if (i < items.size() - 1) {
                tg.putString(col, row, separator);
                col += separator.length();
            }
        }
    }
}
