package org.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.Showable;
import org.dawciobiel.shelldialog.cli.dialog.TextLineDialog;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.i18n.Messages;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;

import static org.dawciobiel.shelldialog.cli.style.TextStyle.ofAnsi;

/**
 * Example usage of the {@link TextLineDialog} class.
 */
public class TextLineQuestionExample {

    public static void main(String[] args) {
        String questionTitle = "Provide your answer to the question";

        // 1. Konfiguracja paska nawigacji
        NavigationToolbar toolbar = NavigationToolbar.builder()
                                                     .withArrowsNavigation()
                                                     .withEnterAccept()
                                                     .withEscapeCancel()
                                                     .build();

        // 2. Konfiguracja motywu
        // @formatter:off
        DialogTheme theme = DialogTheme.builder()
                                       .borderStyle(ofAnsi(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                                       .titleStyle(ofAnsi(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                                       .inputStyle(ofAnsi(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK))
                                       .navigationStyle(ofAnsi(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
                                       .build();
        // @formatter:on

        // 3. Budowa samego dialogu
        // @formatter:off
        TextLineDialog dialog =
                new TextLineDialog.Builder("Question title for your answer ?")
                                    .navigationToolbar(toolbar)
                                    .theme(theme)
                                    .build();
        // @formatter:off

        Value result = dialog.show();
        handleResult(questionTitle, result);
    }

    private static void handleResult(String questionTitle, Value result) {
        switch (result) {
            case TextValue v -> {
                if (v.value()
                     .equals(Showable.DIALOG_CANCELED_FLAG)) {
                    System.out.println("User cancelled the dialog");
                } else {
                    System.out.printf("Question: [ %s ]\nAnswer: [ %s ]\n", questionTitle, v.value());
                }
            }
            case ErrorValue v -> handleError(v);
            default ->
                    System.out.println("Unexpected result type: " + result.getClass()
                                                                          .getSimpleName());
        }
    }

    private static void handleError(ErrorValue v) {
        System.err.println(Messages.getString("error.occurred") + ":\n\n\t" + v.message() + "\n");
        System.err.println(Messages.getString("error.terminal.stream"));
    }

}
