package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardContext;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardSummaryStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardTextStep;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.validation.InputValidator;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link WizardDialog}.
 */
public final class WizardExample {

    private WizardExample() {
    }

    private record SetupData(String username, String targetDirectory) {
    }

    /**
     * Runs the wizard dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                .validationMessageStyle(TextStyle.of(TextColor.ANSI.RED_BRIGHT, TextColor.ANSI.DEFAULT))
                .navigationStyle(TextStyle.of(TextColor.ANSI.MAGENTA_BRIGHT, TextColor.ANSI.DEFAULT))
                .build();

        List<WizardStep> steps = List.of(
                WizardTextStep.builder("Account", "Enter a username", "username")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Username is required."))
                        .build(),
                WizardTextStep.builder("Location", "Enter a target directory", "targetDirectory")
                        .withInitialValue("./output")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Target directory is required."))
                        .build(),
                WizardSummaryStep.of("Summary", WizardExample::summaryLines)
        );

        WizardDialog<SetupData> dialog = new WizardDialog.Builder<SetupData>("Setup Wizard", steps)
                .withTheme(theme)
                .withResultMapper(context -> new SetupData(
                        context.getString("username"),
                        context.getString("targetDirectory")
                ))
                .build();

        Optional<SetupData> result = dialog.show();
        if (result.isEmpty()) {
            out.println("Wizard cancelled.");
            return;
        }

        SetupData data = result.get();
        out.println("Username: " + data.username());
        out.println("Target directory: " + data.targetDirectory());
    }

    private static List<String> summaryLines(WizardContext context) {
        return List.of(
                "Review your setup:",
                "User: " + context.getString("username"),
                "Target: " + context.getString("targetDirectory")
        );
    }
}
