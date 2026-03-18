package org.dawciobiel.shelldialog.cli.navigation;

import org.dawciobiel.shelldialog.cli.i18n.Messages;

public final class NavigationLabels {

    // Separators between UI elements
    public static final String SEP_ITEM = Messages.getString("navigationtoolbar.sep_item");
    public static final String SEP_HOTKEYLABEL = Messages.getString("navigationtoolbar.sep_hotkeylabel");

    // Arrow characters used as indicators/markers in menus
    public static final String MARKER_EMPTY = Messages.getString("marker.empty");
    public static final String MARKER_LEFT = Messages.getString("marker.left");
    public static final String MARKER_RIGHT = Messages.getString("marker.right");

    // Key names (physical markings on the keyboard)
    public static final String KEY_ARROWS = Messages.getString("navigationtoolbar.key_arrows");
    public static final String KEY_ENTER = Messages.getString("navigationtoolbar.key_enter");
    public static final String KEY_ESCAPE = Messages.getString("navigationtoolbar.key_escape");

    // Actions (what the key does)
    public static final String ACTION_NAVIGATION = Messages.getString("navigationtoolbar.action_navigation");
    public static final String ACTION_ACCEPT = Messages.getString("navigationtoolbar.action_accept");
    public static final String ACTION_CANCEL = Messages.getString("navigationtoolbar.action_cancel");


    private NavigationLabels() {
    }
}