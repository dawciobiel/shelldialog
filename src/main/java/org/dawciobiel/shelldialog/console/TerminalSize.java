package org.dawciobiel.shelldialog.console;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class TerminalSize {

    private static final int FALLBACK_WIDTH = 80;

    public static int getWidth() {
        try (Terminal terminal = new DefaultTerminalFactory().createTerminal()) {
            return terminal.getTerminalSize().getColumns();
        } catch (IOException e) {
            return FALLBACK_WIDTH;
        }
    }
}