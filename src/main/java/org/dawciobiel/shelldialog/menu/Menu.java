package org.dawciobiel.shelldialog.menu;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.dawciobiel.shelldialog.console.ConsoleColors;
import org.dawciobiel.shelldialog.console.Messages;
import org.dawciobiel.shelldialog.console.SmartConsole;
import org.dawciobiel.shelldialog.console.TerminalSize;
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

import static java.lang.System.out;

public class Menu {

    private static final String LINE_BREAK = "\n";
    private static final String ERROR_MESSAGE_TERMINAL = Messages.getString("error.terminal");

    private final String[] menuItems;
    private final BorderType borderType;

    private Menu(String[] menuItems, BorderType borderType) {
        this.menuItems = menuItems;
        this.borderType = borderType;
    }

    @SuppressWarnings("unused")
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
        int selectedIndex = 1; // Index value `0` is header, not first menuItem!

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
            printMenu(selectedIndex);
            selectedIndex = processInput(screen, selectedIndex);
            SmartConsole.showCursor();
        }
        out.flush();
        return selectedIndex;
    }

    private int processInput(Screen screen, int selectedIndex) throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            if (type == KeyType.ArrowUp) {
                if (selectedIndex > 1) selectedIndex--;
                printMenu(selectedIndex);
            } else if (type == KeyType.ArrowDown) {
                if (selectedIndex < menuItems.length - 1) selectedIndex++;
                printMenu(selectedIndex);
            } else if (type == KeyType.Enter) {
                return selectedIndex;
            } else if (type == KeyType.Escape) {
                return -1;
            }
        }
    }

    private void printMenu(int selectedIndex) {
        SmartConsole.clearScreen();
        SmartConsole.hideCursor();
        printTitle();
        printMenuItems(selectedIndex);
        printNavigationInstructions();
    }

    private void printTitle() {
        System.out.println(buildTitle());
    }

    private void printMenuItems(int selectedIndex) {
        for (int i = 1; i < menuItems.length; i++) {
            System.out.println(i == selectedIndex ? formatSelectedItem(i) : formatUnselectedItem(i));
        }
    }

    private void printNavigationInstructions() {
        System.out.println(LINE_BREAK + " " + Navigation.NAVIGATION_TEXT_COLOR
                + Navigation.NAVIGATION_ARROWS + " " + Navigation.NAVIGATION_TEXT
                + " " + Navigation.NAVIGATION_TEXT_DELIMITER
                + " " + Navigation.NAVIGATION_ACCEPT_CHARACTER + " " + Navigation.NAVIGATION_ACCEPT_TEXT
                + " " + Navigation.NAVIGATION_TEXT_ACCEPT
                + ConsoleColors.RESET);
    }

    private String buildTitle() {
        int terminalWidth = TerminalSize.getWidth();
        int innerWidth = terminalWidth - 2; // space for side border characters
        List<String> wrappedLines = TextWrapper.wrap(menuItems[0], innerWidth - 1); // -1 for left padding

        return switch (borderType) {
            case BORDER_ALL -> buildTitleFull(innerWidth, wrappedLines);
            case BORDER_HORIZONTAL -> buildTitleHorizontal(innerWidth, wrappedLines);
            case BORDER_VERTICAL -> buildTitleVertical(innerWidth, wrappedLines);
            case BORDER_NO -> buildTitleNone(innerWidth, wrappedLines);
        };
    }

    private String buildTitleFull(int innerWidth, List<String> lines) {
        String borderLine = BorderLine.HORIZONTAL.repeat(innerWidth);
        return buildTopLine(borderLine)
                + buildContentLines(BorderLine.VERTICAL, BorderLine.VERTICAL, innerWidth, lines)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleHorizontal(int innerWidth, List<String> lines) {
        String borderLine = BorderLine.HORIZONTAL.repeat(innerWidth);
        return buildTopLine(borderLine)
                + buildContentLines(BorderLine.NO, BorderLine.NO, innerWidth, lines)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleVertical(int innerWidth, List<String> lines) {
        String borderLine = BorderLine.NO.repeat(innerWidth);
        return buildTopLine(borderLine)
                + buildContentLines(BorderLine.VERTICAL, BorderLine.VERTICAL, innerWidth, lines)
                + buildBottomLine(BorderLine.BOTTOM_LEFT, borderLine, BorderLine.BOTTOM_RIGHT);
    }

    private String buildTitleNone(int innerWidth, List<String> lines) {
        String borderLine = BorderLine.NO.repeat(innerWidth);
        return buildTopLine(borderLine)
                + buildContentLines(BorderLine.NO, BorderLine.NO, innerWidth, lines)
                + buildBottomLine(BorderLine.NO, borderLine, BorderLine.NO);
    }

    private String buildTopLine(String borderLine) {
        return ConsoleColors.BLUE
                + BorderLine.TOP_LEFT + borderLine + BorderLine.TOP_RIGHT
                + ConsoleColors.RESET + LINE_BREAK;
    }

    private String buildBottomLine(String left, String borderLine, String right) {
        return ConsoleColors.BLUE
                + left + borderLine + right
                + ConsoleColors.RESET + LINE_BREAK;
    }

    private String buildContentLines(String leftBorder, String rightBorder,
                                     int innerWidth, List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            int padding = Math.max(0, innerWidth - line.length() - 1); // -1 for left padding
            sb.append(ConsoleColors.BLUE)
                    .append(leftBorder)
                    .append(" ")                    // left padding
                    .append(line)
                    .append(" ".repeat(padding))
                    .append(rightBorder)
                    .append(ConsoleColors.RESET)
                    .append(LINE_BREAK);
        }
        return sb.toString();
    }

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
