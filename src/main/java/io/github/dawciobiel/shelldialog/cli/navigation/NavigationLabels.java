package io.github.dawciobiel.shelldialog.cli.navigation;

import io.github.dawciobiel.shelldialog.cli.i18n.Messages;
import io.github.dawciobiel.shelldialog.cli.i18n.UIProperties;

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
    /** Label describing the tab key. */
    public static final String KEY_TAB = UIProperties.getString("navigationtoolbar.key_tab");
    /** Label describing the F2 key. */
    public static final String KEY_F2 = UIProperties.getString("navigationtoolbar.key_f2");
    /** Label describing the F5 key. */
    public static final String KEY_F5 = UIProperties.getString("navigationtoolbar.key_f5");
    /** Label describing the Home key. */
    public static final String KEY_HOME = UIProperties.getString("navigationtoolbar.key_home");
    /** Label describing the End key. */
    public static final String KEY_END = UIProperties.getString("navigationtoolbar.key_end");

    // Actions (what the key does)
    /** Action label for navigation. */
    public static final String ACTION_NAVIGATION = Messages.getString("navigationtoolbar.action_navigation");
    /** Action label for selecting an item. */
    public static final String ACTION_SELECT = Messages.getString("navigationtoolbar.action_select");
    /** Action label for accepting a dialog. */
    public static final String ACTION_ACCEPT = Messages.getString("navigationtoolbar.action_accept");
    /** Action label for canceling a dialog. */
    public static final String ACTION_CANCEL = Messages.getString("navigationtoolbar.action_cancel");
    /** Action label for returning to home directory. */
    public static final String ACTION_HOME = Messages.getString("navigationtoolbar.action_home");
    /** Action label for refreshing contents. */
    public static final String ACTION_REFRESH = Messages.getString("navigationtoolbar.action_refresh");
    /** Action label for returning to current working directory. */
    public static final String ACTION_CWD = Messages.getString("navigationtoolbar.action_cwd");
    /** Action label for confirming a message. */
    public static final String ACTION_OK = Messages.getString("navigationtoolbar.action_ok");
    /** Action label for moving to the next form field. */
    public static final String ACTION_NEXT_FIELD = Messages.getString("navigationtoolbar.action_next_field");
    /** Action label for toggling hidden files visibility. */
    public static final String ACTION_HIDDEN_FILES = Messages.getString("navigationtoolbar.action_hidden_files");


    private NavigationLabels() {
    }
}
