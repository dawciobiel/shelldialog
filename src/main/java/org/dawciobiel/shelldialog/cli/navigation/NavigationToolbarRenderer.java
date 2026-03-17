package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.util.List;

public final class NavigationToolbarRenderer {

    private final TextColor hotkeyColor;
    private final TextColor labelColor;
    private final TextColor backgroundColor;

    public NavigationToolbarRenderer(TextColor hotkeyColor, TextColor labelColor, TextColor backgroundColor) {
        this.hotkeyColor = hotkeyColor;
        this.labelColor = labelColor;
        this.backgroundColor = backgroundColor;
    }

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