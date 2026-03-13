package org.dawciobiel.shelldialog.console.navigation;

import org.dawciobiel.shelldialog.console.ConsoleColors;

public final class Arrow {

    public static final String ARROW_LEFT = "▶"; // "⧐";
    public static final String ARROW_RIGHT = "◀"; // "⧏";
    public static final String ARROW_COLOR = ConsoleColors.RED;

    private Arrow() {
        throw new UnsupportedOperationException("BorderLine is a utility class and cannot be instantiated");
    }

}
