package org.dawciobiel.shelldialog.cli.style;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.navigation.NavigationLabels;

/**
 * Provides marker characters and colors used to highlight menu selections.
 */
public final class Arrow {

    // Arrows
    // @formatter:off
    /** Marker used when no selection arrow should be shown. */
    public static final String MARKER_EMPTY = NavigationLabels.MARKER_EMPTY;
    /** Left-side marker used for the selected menu item. */
    public static final String MARKER_EFT = NavigationLabels.MARKER_LEFT;
    /** Right-side marker used for the selected menu item. */
    public static final String MARKER_RIGHT = NavigationLabels.MARKER_RIGHT;

    // Colors
    /** Foreground color used for selection markers. */
    public static final TextColor COLOR = TextColor.ANSI.RED_BRIGHT;
    /** Background color used for selection markers. */
    public static final TextColor BG_COLOR = TextColor.ANSI.DEFAULT;
    // @formatter:on

    private Arrow() {
        throw new UnsupportedOperationException("Arrow is a utility class and cannot be instantiated");
    }
}
