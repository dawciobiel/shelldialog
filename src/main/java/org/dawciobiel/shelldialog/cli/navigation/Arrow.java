package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;

public final class Arrow {

    // Arrows
    // @formatter:off
    public static final String ARROW_EMPTY = NavigationLabels.ARROW_EMPTY;
    public static final String ARROW_LEFT = NavigationLabels.ARROW_LEFT;
    public static final String ARROW_RIGHT = NavigationLabels.ARROW_RIGHT;

    // Colors
    public static final TextColor ARROW_COLOR = TextColor.ANSI.RED_BRIGHT;
    public static final TextColor ARROW_BG_COLOR = TextColor.ANSI.DEFAULT;
    // @formatter:on

    private Arrow() {
        throw new UnsupportedOperationException("Arrow is a utility class and cannot be instantiated");
    }
}
