package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.MessageDialog;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link MessageDialog}.
 */
public class MessageExample {

    private MessageExample() {
    }

    /**
     * Runs the message dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        MessageDialog dialog = buildDialog();
        Optional<Boolean> result = dialog.show();

        if (result.isPresent() && result.get()) {
            out.println("User acknowledged the message.");
        } else {
            out.println("User cancelled/dismissed the message.");
        }
    }

    static MessageDialog buildDialog() {
        DialogTheme theme = DialogTheme.darkTheme();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("System Notification")
                .withTitleColor(TextColor.ANSI.CYAN_BRIGHT)
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("The background synchronization process has completed successfully.\nAll your files are up to date.")
                .withTheme(theme)
                .build();

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withEnterOK()
                                .withEscapeCancel()
                                .build()
                )
                .withTheme(theme)
                .build();

        return new MessageDialog.Builder(titleArea, contentArea, navigationArea)
                .withBorder(true)
                .withBorderColor(TextColor.ANSI.CYAN)
                .withTheme(theme)
                .build();
    }
}
