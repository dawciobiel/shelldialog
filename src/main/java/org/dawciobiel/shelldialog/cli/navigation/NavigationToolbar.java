package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

public final class Navigation {

    // Delimiters
    public static final String NAVIGATION_TEXT_DELIMITER_SPACER = Messages.getString("navigation.delimiter.spacer");
    public static final String NAVIGATION_TEXT_DELIMITER_PIPE = Messages.getString("navigation.delimiter.pipe");

    //  ↑↓ Navigation
    public static final String NAVIGATION_ARROWS = Messages.getString("navigation.arrows");
    public static final String NAVIGATION_NAVIGATION = Messages.getString("navigation.navigation");

    //  ↵ Accept
    public static final String NAVIGATION_ENTER = Messages.getString("navigation.enter");
    public static final String NAVIGATION_ACCEPT = Messages.getString("navigation.accept");

    // Esc Cancel
    public static final String NAVIGATION_ESC = Messages.getString("navigation.esc");
    public static final String NAVIGATION_CANCEL = Messages.getString("navigation.cancel");

    // Menu items colors
    public static final TextColor MENUITEM_COLOR = TextColor.ANSI.DEFAULT;
    public static final TextColor MENUITEM_BACKGROUND_COLOR = TextColor.ANSI.DEFAULT;

    // Menu items selected colors
    public static final TextColor MENUITEM_SELECTED_COLOR = TextColor.ANSI.WHITE;
    public static final TextColor MENUITEM_SELECTED_BACKGROUND_COLOR = TextColor.ANSI.BLUE;

    // Navigation hotkeys colors
    public static final TextColor NAVIGATION_HOTKEYS_COLOR = TextColor.ANSI.BLACK_BRIGHT; // ANSI GREY
    public static final TextColor NAVIGATION_HOTKEYS_BACKGROUND_COLOR = TextColor.ANSI.DEFAULT; // ANSI GREY

    //todo Colors value should be stored in properties file as config
    private Navigation() {
        throw new UnsupportedOperationException(Messages.getString("error.navigation.instantiation"));
    }

}
