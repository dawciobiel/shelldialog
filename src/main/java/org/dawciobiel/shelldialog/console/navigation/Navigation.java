package org.dawciobiel.shelldialog.console.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.console.Messages;

public final class Navigation {

    public static final String NAVIGATION_TEXT = Messages.getString("navigation.text");
    public static final String NAVIGATION_TEXT_ACCEPT = Messages.getString("navigation.text.accept");
    public static final String NAVIGATION_ARROWS = "↑↓";
    public static final String NAVIGATION_TEXT_DELIMITER = "|";
    public static final String NAVIGATION_ACCEPT_CHARACTER = "↵";
    public static final String NAVIGATION_ACCEPT_TEXT = Messages.getString("navigation.accept.text");
    public static final TextColor NAVIGATION_TEXT_COLOR = TextColor.ANSI.BLACK_BRIGHT; // ANSI GREY

    public static final TextColor SELECTED_ITEM_TEXT_COLOR = TextColor.ANSI.WHITE;
    public static final TextColor SELECTED_ITEM_BACKGROUND_COLOR = TextColor.ANSI.RED;

    private Navigation() {
        throw new UnsupportedOperationException(Messages.getString("error.navigation.instantiation"));
    }

}
