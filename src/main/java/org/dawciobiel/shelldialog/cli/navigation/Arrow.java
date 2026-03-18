package org.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.TextColor;

public final class Arrow {

    // Arrows
    // @formatter:off
    public static final String MARKER_EMPTY = NavigationLabels.MARKER_EMPTY;
    public static final String MARKER_EFT = NavigationLabels.MARKER_LEFT;
    public static final String MARKER_RIGHT = NavigationLabels.MARKER_RIGHT;

    // Colors
    public static final TextColor COLOR = TextColor.ANSI.RED_BRIGHT;
    public static final TextColor BG_COLOR = TextColor.ANSI.DEFAULT;
    // @formatter:on

    private Arrow() {
        throw new UnsupportedOperationException("Arrow is a utility class and cannot be instantiated");
    }
}
