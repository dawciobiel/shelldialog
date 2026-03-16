package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

public final class NavigationToolbar {

    // Delimiters
    public static final String DELIMITER_SPACER = Messages.getString("navigation.delimiter.spacer");
    public static final String DELIMITER_PIPE = Messages.getString("navigation.delimiter.pipe");

    //  ↑↓ Navigation
    public static final String ARROWS = Messages.getString("navigation.arrows");
    public static final String NAVIGATION = Messages.getString("navigation.navigation");

    public static final String TYPE_YOUR_ANSWER = Messages.getString("navigation.type-your-answer");

    //  ↵ Accept
    public static final String ENTER = Messages.getString("navigation.enter");
    public static final String ACCEPT = Messages.getString("navigation.accept");

    // Esc Cancel
    public static final String ESC = Messages.getString("navigation.esc");
    public static final String CANCEL = Messages.getString("navigation.cancel");

    // Menu items colors
    public static final TextColor MENUITEM_COLOR = TextColor.ANSI.DEFAULT;
    public static final TextColor MENUITEM_BG_COLOR = TextColor.ANSI.DEFAULT;

    // Menu items selected colors
    public static final TextColor MENUITEM_SELECTED_COLOR = TextColor.ANSI.WHITE;
    public static final TextColor MENUITEM_SELECTED_BG_COLOR = TextColor.ANSI.BLUE;

    // Navigation hotkeys colors
    public static final TextColor TOOLBAR_HOTKEYS_COLOR = TextColor.ANSI.BLACK_BRIGHT; // ANSI GREY
    public static final TextColor TOOLBAR_HOTKEYS_BG_COLOR = TextColor.ANSI.DEFAULT; // ANSI GREY

    //todo Colors value should be stored in properties file as config
    private NavigationToolbar() {
        throw new UnsupportedOperationException(Messages.getString("error.navigation.instantiation"));
    }

}
