package org.dawciobiel.shelldialog.menu;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.dawciobiel.shelldialog.console.Messages;
import org.dawciobiel.shelldialog.console.TextWrapper;
import org.dawciobiel.shelldialog.console.header.border.BorderLine;
import org.dawciobiel.shelldialog.console.header.border.BorderType;
import org.dawciobiel.shelldialog.console.navigation.Arrow;
import org.dawciobiel.shelldialog.console.navigation.Navigation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Menu {

    private static final String ERROR_MESSAGE_TERMINAL = Messages.getString("error.terminal");

    private final String[] menuItems;

    private Menu(String[] menuItems, BorderType borderType) {
        this.menuItems = menuItems;
    }

    public static Integer create(String[] menuItems) {
        return create(menuItems, BorderType.BORDER_ALL);
    }

    public static Integer create(String[] menuItems, BorderType borderType) {
        try {
            return new Menu(menuItems, borderType).run();
        } catch (IOException e) {
            System.err.println(ERROR_MESSAGE_TERMINAL + e.getMessage());
            return -1;
        }
    }

    private Integer run() throws IOException {
        int selectedIndex = 1;

        FileInputStream ttyInput = new FileInputStream("/dev/tty");
        FileOutputStream ttyOutput = new FileOutputStream("/dev/tty");

        DefaultTerminalFactory factory = new DefaultTerminalFactory(
                ttyOutput,
                ttyInput,
                StandardCharsets.UTF_8
        );
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
                    return selectedIndex;
                } else if (type == KeyType.Escape) {
                    return -1;
                }
            }
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

    private int drawTitle(TextGraphics tg, int startRow, int terminalWidth) {
        int innerWidth = terminalWidth - 2;
        List<String> wrappedLines = TextWrapper.wrap(menuItems[0], innerWidth - 1);
        
        tg.setForegroundColor(TextColor.ANSI.BLUE);

        String topBorder = BorderLine.TOP_LEFT + BorderLine.HORIZONTAL.repeat(innerWidth) + BorderLine.TOP_RIGHT;
        tg.putString(0, startRow++, topBorder);

        for (String line : wrappedLines) {
            tg.putString(0, startRow, BorderLine.VERTICAL);
            tg.putString(2, startRow, line);
            tg.putString(terminalWidth - 1, startRow, BorderLine.VERTICAL);
            startRow++;
        }

        String bottomBorder = BorderLine.BOTTOM_LEFT + BorderLine.HORIZONTAL.repeat(innerWidth) + BorderLine.BOTTOM_RIGHT;
        tg.putString(0, startRow++, bottomBorder);

        return startRow;
    }

    private void drawSelectedItem(TextGraphics tg, int row, int index) {
        // Draw Left Arrow
        tg.setForegroundColor(Arrow.ARROW_COLOR);
        tg.putString(0, row, Arrow.ARROW_LEFT);

        // Draw Text
        tg.setForegroundColor(Navigation.SELECTED_ITEM_TEXT_COLOR);
        tg.setBackgroundColor(Navigation.SELECTED_ITEM_BACKGROUND_COLOR);
        tg.putString(2, row, menuItems[index]);

        // Reset Background for Right Arrow
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        tg.setForegroundColor(Arrow.ARROW_COLOR);
        tg.putString(2 + menuItems[index].length() + 1, row, Arrow.ARROW_RIGHT);
    }

    private void drawUnselectedItem(TextGraphics tg, int row, int index) {
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        tg.putString(3, row, menuItems[index]);
    }

    private void drawNavigation(TextGraphics tg, int row) {
        tg.setForegroundColor(Navigation.NAVIGATION_TEXT_COLOR);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        
        String nav = " " + Navigation.NAVIGATION_ARROWS + " " + Navigation.NAVIGATION_TEXT 
                   + " " + Navigation.NAVIGATION_TEXT_DELIMITER 
                   + " " + Navigation.NAVIGATION_ACCEPT_CHARACTER + " " + Navigation.NAVIGATION_ACCEPT_TEXT 
                   + " " + Navigation.NAVIGATION_TEXT_ACCEPT;
        
        tg.putString(0, row, nav);
    }
}
