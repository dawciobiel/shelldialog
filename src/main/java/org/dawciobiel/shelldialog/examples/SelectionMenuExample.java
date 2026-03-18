package org.dawciobiel.shelldialog.examples;


import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.SelectionDialog;
import org.dawciobiel.shelldialog.cli.dialog.Showable;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.IntegerValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.i18n.Messages;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;

import static org.dawciobiel.shelldialog.cli.style.TextStyle.ofAnsi;

/**
 * Example usage of the {@link SelectionDialog} class.
 */
public class SelectionMenuExample {

    public static void main(String[] args) {
        // @formatter:off
        String[] menuItems = {
                "Menu Title",
                "1.Item",
                "2.Item",
                "3.Item"
        };
        // @formatter:on

        // @formatter:off
        NavigationToolbar toolbar = NavigationToolbar.builder()
                .withArrowsNavigation()
                .withEnterAccept()
                .withEscapeCancel()
                .build();
        // @formatter:on

        // @formatter:off
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(ofAnsi(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                .titleStyle(ofAnsi(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                .inputStyle(ofAnsi(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                .navigationStyle(ofAnsi(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
                .build();
        // @formatter:on

        // @formatter:off
        SelectionDialog menu = new SelectionDialog.Builder(menuItems)
                .navigationToolbar(toolbar)
                .theme(theme)
                .build();
        // @formatter:on

        Value result = menu.show();

        handleResult(menuItems, result);
    }

    private static void handleResult(String[] menuItems, Value result) {
        switch (result) {
            case IntegerValue v -> System.out.printf("Selected menu item [ %s ]%n", menuItems[v.value()]);
            case TextValue v -> {
                if (Showable.DIALOG_CANCELED_FLAG.equals(v.getTextValue())) {
                    System.out.print("User cancelled the dialog");
                }
            }
            case ErrorValue v -> handleError(v);
        }
    }

    private static void handleError(ErrorValue v) {
        System.err.println(Messages.getString("error.occurred") + ":\n\n\t" + v.message() + "\n");
        System.err.println(Messages.getString("error.terminal.stream"));
    }
}