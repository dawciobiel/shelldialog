package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.Messages;

public final class Arrow {

    public static final String ARROW_LEFT = "▶";
    public static final String ARROW_RIGHT = "◀";
    public static final TextColor ARROW_COLOR = TextColor.ANSI.RED;

    private Arrow() {
        throw new UnsupportedOperationException(Messages.getString("error.arrow.instantiation"));
    }
}
