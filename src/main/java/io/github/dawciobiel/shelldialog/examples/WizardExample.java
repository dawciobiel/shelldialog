package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardContext;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardDirectoryStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardFileStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardPasswordStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardSummaryStep;
import io.github.dawciobiel.shelldialog.cli.dialog.WizardTextStep;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.validation.InputValidator;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link WizardDialog}.
 */
public final class WizardExample {

    private WizardExample() {
    }

    private record SetupData(String username, char[] password, Path targetDirectory, Path configFile) {
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
                        .withDescription("This name will be displayed in the generated configuration summary.")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Username is required."))
                        .build(),
                WizardPasswordStep.builder("Security", "Enter a password", "password")
                        .withDescription("Choose a password for the account created by this setup flow.")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Password is required.").asPasswordValidator())
                        .build(),
                WizardDirectoryStep.builder("Location", "Enter a target directory", "targetDirectory")
                        .withDescription("Pick the directory where generated files should be stored.")
                        .withInitialValue(Path.of("./output"))
                        .build(),
                WizardFileStep.builder("Config", "Enter a config file path", "configFile")
                        .withDescription("Set the path to the configuration file written at the end of the wizard.")
                        .withInitialValue(Path.of("./output/config.properties"))
                        .build(),
                WizardSummaryStep.of("Summary", "Review all collected values before finishing the setup.", WizardExample::summaryLines)
        );

        WizardDialog<SetupData> dialog = new WizardDialog.Builder<SetupData>("Setup Wizard", steps)
                .withTheme(theme)
                .withResultMapper(context -> new SetupData(
                        context.getString("username"),
                        context.getPassword("password"),
                        context.getPath("targetDirectory"),
                        context.getPath("configFile")
                ))
                .build();

        Optional<SetupData> result = dialog.show();
        if (result.isEmpty()) {
            out.println("Wizard cancelled.");
            return;
        }

        SetupData data = result.get();
        out.println("Username: " + data.username());
        out.println("Password length: " + data.password().length);
        out.println("Target directory: " + data.targetDirectory());
        out.println("Config file: " + data.configFile());
    }

    private static List<String> summaryLines(WizardContext context) {
        return List.of(
                "Review your setup:",
                "User: " + context.getString("username"),
                "Password length: " + context.getPassword("password").length,
                "Target: " + context.getPath("targetDirectory"),
                "Config: " + context.getPath("configFile")
        );
    }
}
