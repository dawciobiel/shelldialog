package org.dawciobiel.shelldialog.console.navigation;

import org.dawciobiel.shelldialog.console.ConsoleColors;
import org.dawciobiel.shelldialog.console.Messages;

public final class Arrow {

    public static final String ARROW_LEFT = "▶"; // "⧐";
    public static final String ARROW_RIGHT = "◀"; // "⧏";
    public static final String ARROW_COLOR = ConsoleColors.RED;

    private Arrow() {
        throw new UnsupportedOperationException(Messages.getString("error.arrow.instantiation"));
    }

}
