package org.dawciobiel.shelldialog.menu;

/**
 * Common interface for all menu implementations (CLI, GUI, etc.)
 */
public interface Menu {
    /**
     * Displays the menu and returns the index of the selected item.
     *
     * @return the index of the selected item, or -1 if canceled
     */
    Integer show();
}
