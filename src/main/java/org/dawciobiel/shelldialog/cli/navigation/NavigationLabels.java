package org.dawciobiel.shelldialog.cli.navigation;

import org.dawciobiel.shelldialog.cli.i18n.Messages;
import org.dawciobiel.shelldialog.cli.i18n.UIProperties;

/**
 * Provides localized labels and marker characters used by navigation UI components.
 */
public final class NavigationLabels {

    // Separators between UI elements
    /** Separator inserted between navigation toolbar items. */
    public static final String SEP_ITEM = UIProperties.getString("navigationtoolbar.sep_item");
    /** Separator inserted between an item's hotkey and its label. */
    public static final String SEP_HOTKEYLABEL = UIProperties.getString("navigationtoolbar.sep_hotkeylabel");

    // Arrow characters used as indicators/markers in menus
    /** Empty marker used when no arrow should be displayed. */
    public static final String MARKER_EMPTY = UIProperties.getString("marker.empty");
    /** Left marker used for a selected menu item. */
    public static final String MARKER_LEFT = UIProperties.getString("marker.left");
    /** Right marker used for a selected menu item. */
    public static final String MARKER_RIGHT = UIProperties.getString("marker.right");

    // Key names (physical markings on the keyboard)
    /** Label describing vertical arrow keys. */
    public static final String KEY_ARROWS_VERTICAL = UIProperties.getString("navigationtoolbar.key_arrows_vertical");
    /** Label describing horizontal arrow keys. */
    public static final String KEY_ARROWS_HORIZONTAL = UIProperties.getString("navigationtoolbar.key_arrows_horizontal");
    /** Label describing the space key. */
    public static final String KEY_SPACE = UIProperties.getString("navigationtoolbar.key_space");
    /** Label describing the enter key. */
    public static final String KEY_ENTER = UIProperties.getString("navigationtoolbar.key_enter");
    /** Label describing the escape key. */
    public static final String KEY_ESCAPE = UIProperties.getString("navigationtoolbar.key_escape");

    // Actions (what the key does)
    /** Action label for navigation. */
    public static final String ACTION_NAVIGATION = Messages.getString("navigationtoolbar.action_navigation");
    /** Action label for selecting an item. */
    public static final String ACTION_SELECT = Messages.getString("navigationtoolbar.action_select");
    /** Action label for accepting a dialog. */
    public static final String ACTION_ACCEPT = Messages.getString("navigationtoolbar.action_accept");
    /** Action label for canceling a dialog. */
    public static final String ACTION_CANCEL = Messages.getString("navigationtoolbar.action_cancel");


    private NavigationLabels() {
    }
}
