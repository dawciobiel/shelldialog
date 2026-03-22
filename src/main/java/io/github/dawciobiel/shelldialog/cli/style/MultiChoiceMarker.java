package io.github.dawciobiel.shelldialog.cli.style;

import io.github.dawciobiel.shelldialog.cli.i18n.UIProperties;

/**
 * Provides localized marker strings used by {@code MultiChoiceDialog}.
 */
public final class MultiChoiceMarker {

    /** Marker shown for an unselected option. */
    public static final String UNSELECTED = UIProperties.getString("multichoice.marker.unselected");
    /** Marker shown for a selected option. */
    public static final String SELECTED = UIProperties.getString("multichoice.marker.selected");

    private MultiChoiceMarker() {
        throw new UnsupportedOperationException("MultiChoiceMarker is a utility class and cannot be instantiated");
    }
}
