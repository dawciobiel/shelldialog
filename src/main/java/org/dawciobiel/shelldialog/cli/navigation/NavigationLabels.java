package org.dawciobiel.shelldialog.cli.navigation;

import org.dawciobiel.shelldialog.cli.i18n.Messages;
import org.dawciobiel.shelldialog.cli.i18n.UIProperties;

public final class NavigationLabels {

    // Separators between UI elements
    public static final String SEP_ITEM = UIProperties.getString("navigationtoolbar.sep_item");
    public static final String SEP_HOTKEYLABEL = UIProperties.getString("navigationtoolbar.sep_hotkeylabel");

    // Arrow characters used as indicators/markers in menus
    public static final String MARKER_EMPTY = UIProperties.getString("marker.empty");
    public static final String MARKER_LEFT = UIProperties.getString("marker.left");
    public static final String MARKER_RIGHT = UIProperties.getString("marker.right");

    // Key names (physical markings on the keyboard)
    public static final String KEY_ARROWS = UIProperties.getString("navigationtoolbar.key_arrows");
    public static final String KEY_ENTER = UIProperties.getString("navigationtoolbar.key_enter");
    public static final String KEY_ESCAPE = UIProperties.getString("navigationtoolbar.key_escape");

    // Actions (what the key does)
    public static final String ACTION_NAVIGATION = Messages.getString("navigationtoolbar.action_navigation");
    public static final String ACTION_ACCEPT = Messages.getString("navigationtoolbar.action_accept");
    public static final String ACTION_CANCEL = Messages.getString("navigationtoolbar.action_cancel");


    private NavigationLabels() {
    }
}
