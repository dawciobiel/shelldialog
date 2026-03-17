package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

public final class Arrow {

    //formatters:off
    // Arrows
    public static final String ARROW_LEFT = Messages.getString("arrow.left"); // "▶"
    public static final String ARROW_RIGHT = Messages.getString("arrow.right"); // "◀"

    // Colors
    public static final TextColor ARROW_COLOR = TextColor.ANSI.RED_BRIGHT;
    public static final TextColor ARROW_BG_COLOR = TextColor.ANSI.DEFAULT;
    //formatters:on

    private Arrow() {
        throw new UnsupportedOperationException(Messages.getString("error.arrow.instantiation"));
    }
}
