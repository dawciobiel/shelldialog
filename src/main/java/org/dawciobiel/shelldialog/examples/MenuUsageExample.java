package org.dawciobiel.shelldialog.examples;

import org.dawciobiel.shelldialog.cli.dialog.SelectionMenu;
import org.dawciobiel.shelldialog.cli.dialog.Showable;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.IntegerValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

/**
 * Example usage of the {@link SelectionMenu} class.
 */
public class MenuUsageExample {

    public static void main(String[] args) {
        // Show menu dialog
        String[] menuItems = {"Menu Title", "1.Item", "2.Item", "3.Item"};

        // Messages.setLocale(Locale.of("pl", "PL"));
        Showable menu = new SelectionMenu(menuItems);

        // Parse result
        handleResult(menu.show(), menuItems);
    }

    private static void handleResult(Value result, String[] menuItems) {
        switch (result) {
            case IntegerValue v -> System.out.printf("Selected menu item [ %s ]%n", menuItems[v.value()]);
            case TextValue v -> System.out.printf("User left menu dialog by: %s button%n", v.value());
            case ErrorValue v -> handleError(v);
        }
    }

    private static void handleError(ErrorValue v) {
        System.err.println(Messages.getString("error.occurred") + ":\n\n\t" + v.message() + "\n");
        System.err.println(Messages.getString("error.terminal.stream"));
    }
}
