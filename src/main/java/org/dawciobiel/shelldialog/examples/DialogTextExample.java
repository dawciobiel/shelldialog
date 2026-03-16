package org.dawciobiel.shelldialog.examples;

import org.dawciobiel.shelldialog.cli.dialog.TextLineQuestion;
import org.dawciobiel.shelldialog.cli.dialog.SelectionMenu;
import org.dawciobiel.shelldialog.cli.dialog.Showable;
import org.dawciobiel.shelldialog.cli.dialog.result.ErrorValue;
import org.dawciobiel.shelldialog.cli.dialog.result.TextValue;
import org.dawciobiel.shelldialog.cli.dialog.result.Value;
import org.dawciobiel.shelldialog.cli.i18n.Messages;

/**
 * Example usage of the {@link SelectionMenu} class.
 */
public class DialogTextExample {

    public static void main(String[] args) {
        // Show menu dialog
        String questionTitle = "Provide your answer to the question";

        // Messages.setLocale(Locale.of("pl", "PL"));
        Showable question = new TextLineQuestion(questionTitle);

        // Parse result
        handleResult(question.show(), questionTitle);
    }

    private static void handleResult(Value result, String questionTitle) {
        switch (result) {
            case TextValue v -> {
                if (v.value().equals("Esc")) {
                    System.out.println("User cancelled the dialog.");
                } else {
                    System.out.printf("Question: [ %s ]\nAnswer: [ %s ]\n", questionTitle, v.value());
                }
            }
            case ErrorValue v -> handleError(v);
            default -> System.out.println("Unexpected result type: " + result.getClass().getSimpleName());
        }
    }

    private static void handleError(ErrorValue v) {
        System.err.println(Messages.getString("error.occurred") + ":\n\n\t" + v.message() + "\n");
        System.err.println(Messages.getString("error.terminal.stream"));
    }

}
