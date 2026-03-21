package org.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.PasswordDialog;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.InputArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Arrays;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link PasswordDialog}.
 */
public class PasswordExample {

    private PasswordExample() {
    }

    /**
     * Runs the password dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                .build();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Enter your password")
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("The typed password is masked on screen.")
                .withTheme(theme)
                .build();

        InputArea inputArea = new InputArea.Builder()
                .withTheme(theme)
                .build();

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withEnterAccept()
                                .withEscapeCancel()
                                .build()
                )
                .withRenderer(
                        new NavigationToolbarRenderer(
                                TextColor.ANSI.MAGENTA_BRIGHT,
                                TextColor.ANSI.WHITE,
                                TextColor.ANSI.DEFAULT
                        )
                )
                .build();

        PasswordDialog dialog = new PasswordDialog.Builder(titleArea, contentArea, inputArea, navigationArea)
                .withTheme(theme)
                .build();

        Optional<char[]> result = dialog.show();

        if (result.isPresent()) {
            char[] password = result.get();
            out.println("Password length: " + password.length);
            Arrays.fill(password, '\0');
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
