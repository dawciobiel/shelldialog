package org.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.TextLineDialog;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.InputArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link TextLineDialog}.
 */
public class TextLineExample {

    private TextLineExample() {
    }

    /**
     * Runs the text-line dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        DialogTheme theme = DialogTheme.builder()
                                       .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                                       .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                                       .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                                       .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                                       .menuItemStyle(TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT))
                                       .menuItemSelectedStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                                       .build();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Please enter your name")
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("Your answer will be used in the greeting.")
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

        InputArea inputArea = new InputArea.Builder()
                .withTheme(theme)
                .build();

        TextLineDialog dialog = new TextLineDialog.Builder(titleArea, contentArea, inputArea, navigationArea)
                .build();

        Optional<String> result = dialog.show();

        if (result.isPresent()) {
            String name = result.get();
            if (name.isEmpty()) {
                out.println("You entered an empty name.");
            } else {
                out.println("Hello, " + name + "!");
            }
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
