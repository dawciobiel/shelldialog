package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.ProgressDialog;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link ProgressDialog}.
 */
public class ProgressExample {

    private ProgressExample() {
    }

    /**
     * Runs the progress dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        ProgressDialog dialog = buildDialog();
        Optional<Boolean> result = dialog.show();

        if (result.orElse(false)) {
            out.println("Operation completed successfully!");
        } else {
            out.println("Operation was cancelled or failed.");
        }
    }

    static ProgressDialog buildDialog() {
        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Performing Long Operation")
                .withTitleColor(TextColor.ANSI.CYAN_BRIGHT)
                .build();

        ContentArea statusArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.WHITE)
                .build();

        DialogTheme theme = DialogTheme.darkTheme();

        return new ProgressDialog.Builder(titleArea, statusArea)
                .withTheme(theme)
                .withTask(reporter -> {
                    int totalSteps = 100;
                    for (int i = 0; i <= totalSteps; i++) {
                        if (reporter.isCancelled()) {
                            break;
                        }
                        
                        double progress = (double) i / totalSteps;
                        String msg = String.format("Processing item %d of %d...", i, totalSteps);
                        reporter.update(progress, msg);
                        
                        Thread.sleep(50); // Simulate work
                    }
                })
                .build();
    }
}
