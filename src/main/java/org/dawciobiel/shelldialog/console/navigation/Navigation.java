package org.dawciobiel.shelldialog.console.navigation;

import org.dawciobiel.shelldialog.console.ConsoleColors;
import org.dawciobiel.shelldialog.console.Messages;

public final class Navigation {

    public static final String NAVIGATION_TEXT = Messages.getString("navigation.text");
    public static final String NAVIGATION_TEXT_ACCEPT = Messages.getString("navigation.text.accept");
    public static final String NAVIGATION_ARROWS = "↑↓";
    public static final String NAVIGATION_TEXT_DELIMITER = "|";
    public static final String NAVIGATION_ACCEPT_CHARACTER = "↵";
    public static final String NAVIGATION_ACCEPT_TEXT = Messages.getString("navigation.accept.text");
    public static final String NAVIGATION_TEXT_COLOR = ConsoleColors.GREY;

    public static final String SELECTED_ITEM_TEXT_COLOR = ConsoleColors.WHITE;
    public static final String SELECTED_ITEM_BACKGROUND_COLOR = ConsoleColors.BG_RED;

    private Navigation() {
        throw new UnsupportedOperationException(Messages.getString("error.navigation.instantiation"));
    }

}
