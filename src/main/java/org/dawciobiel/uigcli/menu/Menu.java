package org.dawciobiel.uigcli.menu;

import org.dawciobiel.uigcli.console.ConsoleColors;
import org.dawciobiel.uigcli.console.SmartConsole;
import org.dawciobiel.uigcli.console.header.Header;
import org.dawciobiel.uigcli.console.header.border.BorderLine;
import org.dawciobiel.uigcli.console.header.border.BorderType;
import org.dawciobiel.uigcli.console.navigation.Arrow;
import org.dawciobiel.uigcli.console.navigation.Navigation;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

import static java.lang.System.out;

public class Menu {

    // Kody klawiszy zwracane przez JLine
    private static final int KEY_UP = 65;  // ESC [ A
    private static final int KEY_DOWN = 66;  // ESC [ B
    private static final int KEY_ENTER = 13;  // \r
    private static final int KEY_ESC = 27;  // ESC

    private static final String LINE_BREAK = "\n";
    private static final String ERROR_MESSAGE_TERMINAL = "Błąd terminala: "; //todo Todo internationalization

    private final String[] menuItems;
    private final BorderType borderType;
    private int selectedIndex = 1;

    private Menu(String[] menuItems, BorderType borderType) {
        this.menuItems = menuItems;
        this.borderType = borderType;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Core logic
    // -------------------------------------------------------------------------

    private Integer run() throws IOException {
        // Zarejestruj hook PRZED otwarciem terminala
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                org.fusesource.jansi.AnsiConsole.systemUninstall();
            } catch (Exception ignored) {
                // celowo ignoruj — Maven wywoła to ponownie i rzuci wyjątek
            }
        }));

        try (Terminal terminal = buildTerminal()) {
            terminal.enterRawMode();
            drawMenu();
            processInput(terminal);
            SmartConsole.showCursor();
            terminal.echo(true);
            terminal.writer().flush();
        }
        out.flush();
        return selectedIndex;
    }

    private void processInput(Terminal terminal) throws IOException {
        while (true) {
            int key = terminal.reader().read();
            if (key == KEY_ESC) {
                handleArrowKey(terminal);
            } else if (key == KEY_ENTER || key == '\n') {
                break;
            }
        }
    }

    private void handleArrowKey(Terminal terminal) throws IOException {
        terminal.reader().read(); // pomiń '['
        int arrow = terminal.reader().read();
        switch (arrow) {
            case KEY_UP -> {
                if (selectedIndex > 1) selectedIndex--;
                drawMenu();
            }
            case KEY_DOWN -> {
                if (selectedIndex < menuItems.length - 1) selectedIndex++;
                drawMenu();
            }
        }
    }

    private Terminal buildTerminal() throws IOException {
        try {
            return TerminalBuilder.builder()
                    .system(true)
                    .jansi(true)
                    .dumb(false)
//                    .streams(System.in, System.out)
                    .jna(false)
                    .provider("jansi")
                    .build();
        } catch (IllegalStateException e) {
            return TerminalBuilder.builder()
                    .dumb(true)
                    .build();
        }
    }

    // -------------------------------------------------------------------------
    // Rendering
    // -------------------------------------------------------------------------

    private void drawMenu() {
        SmartConsole.clearScreen();
        SmartConsole.hideCursor();
        printTitle();
        printMenuItems();
        printNavigationInstructions();
    }

    private void printTitle() {
        IO.println(buildTitle());
    }

    private void printMenuItems() {
        for (int i = 1; i < menuItems.length; i++) {
            IO.println(i == selectedIndex ? formatSelectedItem(i) : formatUnselectedItem(i));
        }
    }

    private void printNavigationInstructions() {
        IO.println(LINE_BREAK + " " + Navigation.NAVIGATION_TEXT_COLOR
                + Navigation.NAVIGATION_ARROWS + " " + Navigation.NAVIGATION_TEXT
                + " " + Navigation.NAVIGATION_TEXT_DELIMITER
                + " " + Navigation.NAVIGATION_ACCEPT_CHARACTER + " " + Navigation.NAVIGATION_ACCEPT_TEXT
                + " " + Navigation.NAVIGATION_TEXT_ACCEPT
                + ConsoleColors.RESET);
    }

    // -------------------------------------------------------------------------
    // Title builders
    // -------------------------------------------------------------------------

    private String buildTitle() {
        return switch (borderType) {
            case BORDER_ALL -> buildTitleFull();
            case BORDER_HORIZONTAL -> buildTitleHorizontal();
            case BORDER_VERTICAL -> buildTitleVertical();
            case BORDER_NO -> buildTitleNone();
        };
    }

    private String buildTitleFull() {
        String borderLine = buildBorderLine(true);
        return buildTopLine(borderLine)
                + buildContentLine(BorderLine.VERTICAL, BorderLine.VERTICAL)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleHorizontal() {
        String borderLine = buildBorderLine(true);
        return buildTopLine(borderLine)
                + buildContentLine(BorderLine.NO, BorderLine.NO)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleVertical() {
        String borderLine = buildBorderLine(false);
        return buildTopLine(borderLine)
                + buildContentLine(BorderLine.VERTICAL, BorderLine.VERTICAL)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleNone() {
        String borderLine = buildBorderLine(false);
        return buildTopLine(borderLine)
                + buildContentLine(BorderLine.NO, BorderLine.NO)
                + buildBottomLine(BorderLine.NO, borderLine, BorderLine.NO);
    }

    // -------------------------------------------------------------------------
    // Title line helpers
    // -------------------------------------------------------------------------

    private String buildTopLine(String borderLine) {
        return ConsoleColors.BLUE + BorderLine.TOP_LEFT + borderLine + BorderLine.TOP_RIGHT + ConsoleColors.RESET + LINE_BREAK;
    }

    private String buildBottomLine(String left, String borderLine, String right) {
        return ConsoleColors.BLUE + left + borderLine + right + ConsoleColors.RESET + LINE_BREAK;
    }

    private String buildContentLine(String leftBorder, String rightBorder) {
        int padding = Math.max(0, Header.HEADER_WIDTH - menuItems[0].length() - 3);
        return ConsoleColors.BLUE + leftBorder + menuItems[0]
                + " ".repeat(padding)
                + rightBorder + ConsoleColors.RESET + LINE_BREAK;
    }

    private String buildBorderLine(boolean filled) {
        String ch = filled ? BorderLine.HORIZONTAL : BorderLine.NO;
        return ch.repeat(Header.HEADER_WIDTH);
    }

    // -------------------------------------------------------------------------
    // Item formatters
    // -------------------------------------------------------------------------

    private String formatSelectedItem(int index) {
        return Arrow.ARROW_COLOR + " " + Arrow.ARROW_LEFT + " "
                + Navigation.SELECTED_ITEM_TEXT_COLOR + Navigation.SELECTED_ITEM_BACKGROUND_COLOR
                + menuItems[index]
                + ConsoleColors.RESET + Arrow.ARROW_COLOR + Arrow.ARROW_RIGHT
                + ConsoleColors.RESET;
    }

    private String formatUnselectedItem(int index) {
        return "   " + menuItems[index];
    }
}