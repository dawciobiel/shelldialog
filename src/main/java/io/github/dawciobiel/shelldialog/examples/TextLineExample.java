package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.TextLineDialog;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

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
                                       .validationMessageStyle(TextStyle.of(TextColor.ANSI.RED_BRIGHT, TextColor.ANSI.DEFAULT))
                                       .menuItemStyle(TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT))
                                       .menuItemSelectedStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                                       .build();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Please enter your name")
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("Enter 1-20 characters. Empty input is not allowed.")
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
                .withTheme(theme)
                .withMaxLength(20)
                .withInitialValue("abcdef")
                .withValidator(value -> value.isBlank()
                        ? Optional.of("Name is required.")
                        : Optional.empty())
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
