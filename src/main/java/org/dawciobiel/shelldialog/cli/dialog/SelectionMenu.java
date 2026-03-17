package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.dawciobiel.shelldialog.cli.TextWrapper;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.IntegerValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.header.border.BorderLine;
import org.dawciobiel.shelldialog.cli.header.border.BorderType;
import org.dawciobiel.shelldialog.cli.navigation.Arrow;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * CLI implementation of a selection menu using Lanterna.
 */
public class SelectionMenu implements Showable {

    private static final String INPUT_STREAM = "/dev/tty";
    private static final String OUTPUT_STREAM = "/dev/tty";

    private final String[] menuItems;
    private final BorderType borderType;

    public SelectionMenu(String[] menuItems) {
        this.menuItems = menuItems;
        this.borderType = BorderType.BORDER_ALL;
    }

    public Value show() {
        int selectedIndex = 1;

        FileInputStream ttyInput;
        try {
            ttyInput = new FileInputStream(INPUT_STREAM);
        } catch (FileNotFoundException e) {
            return new ErrorValue(e.getLocalizedMessage());
        }

        FileOutputStream ttyOutput;
        try {
            ttyOutput = new FileOutputStream(OUTPUT_STREAM);
        } catch (FileNotFoundException e) {
            return new ErrorValue(e.getLocalizedMessage());
        }

        DefaultTerminalFactory factory = new DefaultTerminalFactory(ttyOutput, ttyInput, StandardCharsets.UTF_8);
        factory.setForceTextTerminal(true);

        try (Screen screen = factory.createScreen()) {
            screen.startScreen();
            screen.setCursorPosition(null); // Hide cursor

            TextGraphics tg = screen.newTextGraphics();

            while (true) {
                render(screen, tg, selectedIndex);

                KeyStroke key = screen.readInput();
                KeyType type = key.getKeyType();

                if (type == KeyType.ArrowUp) {
                    if (selectedIndex > 1) selectedIndex--;
                } else if (type == KeyType.ArrowDown) {
                    if (selectedIndex < menuItems.length - 1) selectedIndex++;
                } else if (type == KeyType.Enter) {
                    return new IntegerValue(selectedIndex);
                } else if (type == KeyType.Escape) {
                    return new TextValue("Esc");
                }
            }
        } catch (IOException e) {
            return new ErrorValue(e.getLocalizedMessage());
        }
    }

    private void render(Screen screen, TextGraphics tg, int selectedIndex) throws IOException {
        screen.clear();
        int currentRow = 0;
        int terminalWidth = screen.getTerminalSize().getColumns();

        // 1. Draw Title
        currentRow = drawTitle(tg, currentRow, terminalWidth);

        // 2. Draw Menu Items
        for (int i = 1; i < menuItems.length; i++) {
            if (i == selectedIndex) {
                drawSelectedItem(tg, currentRow++, i);
            } else {
                drawUnselectedItem(tg, currentRow++, i);
            }
        }

        // 3. Draw Navigation Instructions
        drawNavigation(tg, currentRow + 1);

        screen.refresh();
    }

    //todo To implement display border based on value `borderType` from .show() method
    private int drawTitle(TextGraphics tg, int startRow, int terminalWidth) {
        int innerWidth = terminalWidth - 2;
        List<String> wrappedLines = TextWrapper.wrap(menuItems[0], innerWidth - 1);

        tg.setForegroundColor(TextColor.ANSI.BLUE);

        // @formatter:off
        String topBorder = BorderLine.DOUBLE_TOP_LEFT
                + BorderLine.DOUBLE_HORIZONTAL.repeat(innerWidth)
                + BorderLine.DOUBLE_TOP_RIGHT;
        // @formatter:on
        tg.putString(0, startRow++, topBorder);

        for (String line : wrappedLines) {
            tg.putString(0, startRow, BorderLine.DOUBLE_VERTICAL);
            tg.putString(2, startRow, line);
            tg.putString(terminalWidth - 1, startRow, BorderLine.DOUBLE_VERTICAL);
            startRow++;
        }
        // @formatter:off
        String bottomBorder = BorderLine.DOUBLE_BOTTOM_LEFT
                        + BorderLine.DOUBLE_HORIZONTAL.repeat(innerWidth)
                        + BorderLine.DOUBLE_BOTTOM_RIGHT;
        // @formatter:on
        tg.putString(0, startRow++, bottomBorder);

        return startRow;
    }

    private void drawSelectedItem(TextGraphics tg, int row, int index) {
        // Draw Left Arrow
        tg.setForegroundColor(Arrow.ARROW_COLOR);
        tg.setBackgroundColor(Arrow.ARROW_BG_COLOR);
        tg.putString(0, row, Arrow.ARROW_LEFT);

        // Draw Text
        tg.setForegroundColor(NavigationToolbar.MENUITEM_SELECTED_COLOR);
        tg.setBackgroundColor(NavigationToolbar.MENUITEM_SELECTED_BG_COLOR);
        tg.putString(2, row, menuItems[index]);

        // Reset Background for Right Arrow
        tg.setForegroundColor(Arrow.ARROW_COLOR);
        tg.setBackgroundColor(Arrow.ARROW_BG_COLOR);
        tg.putString(2 + menuItems[index].length(), row, Arrow.ARROW_RIGHT);
    }

    private void drawUnselectedItem(TextGraphics tg, int row, int index) {
        tg.setForegroundColor(NavigationToolbar.MENUITEM_COLOR);
        tg.setBackgroundColor(NavigationToolbar.MENUITEM_BG_COLOR);
        tg.putString(2, row, menuItems[index]);
    }

    private void drawNavigation(TextGraphics tg, int row) {
        tg.setForegroundColor(NavigationToolbar.TOOLBAR_HOTKEYS_COLOR);
        tg.setBackgroundColor(NavigationToolbar.TOOLBAR_HOTKEYS_BG_COLOR);

        // ↑↓ Navigation | ↵ Accept | Esc Cancel
        // @formatter:off
        String nav = String.join(
                NavigationToolbar.DELIMITER_SPACER,
                NavigationToolbar.ARROWS,
                NavigationToolbar.NAVIGATION,
                NavigationToolbar.DELIMITER_PIPE,
                NavigationToolbar.ENTER,
                NavigationToolbar.ACCEPT,
                NavigationToolbar.DELIMITER_PIPE,
                NavigationToolbar.ESC,
                NavigationToolbar.CANCEL
        );
        // @formatter:on

        tg.putString(0, row, nav);
    }
}
