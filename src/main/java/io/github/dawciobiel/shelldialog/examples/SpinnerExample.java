package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.SpinnerDialog;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link SpinnerDialog}.
 */
public class SpinnerExample {

    private SpinnerExample() {
    }

    /**
     * Runs the spinner dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        SpinnerDialog dialog = buildDialog();
        Optional<Boolean> result = dialog.show();

        if (result.orElse(false)) {
            out.println("Connected successfully!");
        } else {
            out.println("Connection failed or was cancelled.");
        }
    }

    static SpinnerDialog buildDialog() {
        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Connecting to Server")
                .withTitleColor(TextColor.ANSI.MAGENTA_BRIGHT)
                .build();

        ContentArea statusArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.WHITE)
                .build();

        DialogTheme theme = DialogTheme.darkTheme();

        return new SpinnerDialog.Builder(titleArea, statusArea)
                .withTheme(theme)
                .withTask(reporter -> {
                    String[] steps = {
                            "Resolving hostname...",
                            "Establishing handshake...",
                            "Authenticating...",
                            "Downloading configuration...",
                            "Finalizing connection..."
                    };

                    for (String step : steps) {
                        if (reporter.isCancelled()) break;
                        reporter.update(0, step);
                        Thread.sleep(1500); // Simulate network latency
                    }
                })
                // Custom dots spinner
                .withSpinnerFrames(List.of("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"))
                .build();
    }
}
