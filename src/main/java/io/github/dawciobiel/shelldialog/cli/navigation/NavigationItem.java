package io.github.dawciobiel.shelldialog.cli.navigation;

/**
 * Single entry displayed in a navigation toolbar.
 *
 * @param hotkey the key name shown to the user
 * @param label the action label associated with the key
 */
public record NavigationItem(String hotkey, String label) {

}
