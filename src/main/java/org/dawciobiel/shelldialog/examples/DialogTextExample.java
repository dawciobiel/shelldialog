package org.dawciobiel.shelldialog.examples;

import org.dawciobiel.shelldialog.cli.dialog.Menu;
import org.dawciobiel.shelldialog.cli.dialog.QuestionDialog;
import org.dawciobiel.shelldialog.cli.dialog.Showable;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.IntegerValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

/**
 * Example usage of the {@link Menu} class.
 */
public class DialogTextExample {

    public static void main(String[] args) {
        // Show menu dialog
        String questionTitle = "Question title";

        // Messages.setLocale(Locale.of("pl", "PL"));
        Showable question = new QuestionDialog(questionTitle);

        // Parse result
        handleResult(question.show(), questionTitle);
    }

    private static void handleResult(Value result, String questionTitle) {
        switch (result) {
            case IntegerValue v ->
                    System.out.printf("Question: [ %s ]\nAnswer: [ %s ]", v.value(), questionTitle);
            case TextValue v -> System.out.printf("User left menu dialog by: %s button%n", v.value());
            case ErrorValue v -> handleError(v);
        }
    }

    private static void handleError(ErrorValue v) {
        System.err.println(Messages.getString("error.occurred") + ":\n\n\t" + v.message() + "\n");
        System.err.println(Messages.getString("error.terminal.stream"));
    }

}
