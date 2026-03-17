package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import org.dawciobiel.shelldialog.cli.TextWrapper;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.header.border.BorderLine;
import org.dawciobiel.shelldialog.cli.header.border.BorderType;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TextLineQuestion implements Showable {

    private static final String INPUT_STREAM = "/dev/tty";
    private static final String OUTPUT_STREAM = "/dev/tty";

    private final String title;
    private final BorderType borderType;

    public TextLineQuestion(String question) {
        this.title = question;
        this.borderType = BorderType.BORDER_ALL;
    }

    public Value show() {
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

        StringBuilder inputBuffer = new StringBuilder();

        try (Screen screen = factory.createScreen()) {
            screen.startScreen();
            // Cursor will be set in render

            TextGraphics tg = screen.newTextGraphics();

            while (true) {
                render(screen, tg, inputBuffer.toString());

                KeyStroke key = screen.readInput();
                KeyType type = key.getKeyType();

                if (type == KeyType.Enter) {
                    return new TextValue(inputBuffer.toString());
                } else if (type == KeyType.Escape) {
                    return new TextValue(Showable.DIALOG_CANCELED_FLAG);
                } else if (type == KeyType.Backspace) {
                    if (!inputBuffer.isEmpty()) {
                        inputBuffer.setLength(inputBuffer.length() - 1);
                    }
                } else if (type == KeyType.Character) {
                    inputBuffer.append(key.getCharacter());
                }
            }
        } catch (IOException e) {
            return new ErrorValue(e.getLocalizedMessage());
        }
    }

    private void render(Screen screen, TextGraphics tg, String content) throws IOException {
        screen.clear();
        int currentRow = 0;
        int terminalWidth = screen.getTerminalSize().getColumns();

        // 1. Draw Title
        currentRow = drawTitle(tg, currentRow, terminalWidth);

        // 2. Draw Content (Input)
        drawContent(tg, currentRow, content);
        screen.setCursorPosition(new TerminalPosition(2 + content.length(), currentRow));
        currentRow++;

        // 3. Draw Navigation Instructions
        drawNavigation(tg, currentRow + 1);

        screen.refresh();
    }

    //todo To implement display border based on value `borderType` from .show() method
    private int drawTitle(TextGraphics tg, int startRow, int terminalWidth) {
        int innerWidth = terminalWidth - 2;
        List<String> wrappedLines = TextWrapper.wrap(title, innerWidth - 1);

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

    private void drawContent(TextGraphics tg, int row, String content) {
        tg.setForegroundColor(NavigationToolbar.MENUITEM_COLOR);
        tg.setBackgroundColor(NavigationToolbar.MENUITEM_BG_COLOR);
        tg.putString(2, row, content);
    }

    private void drawNavigation(TextGraphics tg, int row) {
        tg.setForegroundColor(NavigationToolbar.TOOLBAR_HOTKEYS_COLOR);
        tg.setBackgroundColor(NavigationToolbar.TOOLBAR_HOTKEYS_BG_COLOR);

        // Expected result: "Type your answer | ↵ Accept | Esc Cancel"
        // @formatter:off
        String nav = String.join(
                NavigationToolbar.DELIMITER_SPACER,
                NavigationToolbar.KEYBOARD_KEYS,
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
