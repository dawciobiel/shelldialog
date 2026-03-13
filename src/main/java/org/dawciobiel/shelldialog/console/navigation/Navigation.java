package org.dawciobiel.shelldialog.console.navigation;

import org.dawciobiel.shelldialog.console.ConsoleColors;

public final class Navigation {

    public static final String NAVIGATION_TEXT = "Nawigacja"; //todo Todo internationalization
    public static final String NAVIGATION_TEXT_ACCEPT = "wybór";  //todo Todo internationalization
    public static final String NAVIGATION_ARROWS = "↑↓";
    public static final String NAVIGATION_TEXT_DELIMITER = "|";
    public static final String NAVIGATION_ACCEPT_CHARACTER = "↵";
    public static final String NAVIGATION_ACCEPT_TEXT = "Enter";
    public static final String NAVIGATION_TEXT_COLOR = ConsoleColors.GREY;

    public static final String SELECTED_ITEM_TEXT_COLOR = ConsoleColors.WHITE;
    public static final String SELECTED_ITEM_BACKGROUND_COLOR = ConsoleColors.BG_RED;

    private Navigation() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }

}
