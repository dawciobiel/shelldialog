package org.dawciobiel.shelldialog.console.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.console.Messages;

public final class Arrow {

    public static final String ARROW_LEFT = "▶";
    public static final String ARROW_RIGHT = "◀";
    public static final TextColor ARROW_COLOR = TextColor.ANSI.RED;

    private Arrow() {
        throw new UnsupportedOperationException(Messages.getString("error.arrow.instantiation"));
    }
}
