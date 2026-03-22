package org.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import org.dawciobiel.shelldialog.cli.dialog.YesNoDialog;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import org.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link YesNoDialog}.
 */
public class YesNoExample {

    private YesNoExample() {
    }

    /**
     * Runs the yes-no dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        DialogTheme theme = DialogTheme.builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                .navigationStyle(TextStyle.of(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
                .build();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Confirm action")
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("Do you want to continue?")
                .withTheme(theme)
                .build();

        ContentArea answerArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.WHITE)
                .withBackgroundColor(TextColor.ANSI.DEFAULT)
                .build();

        ContentArea selectedAnswerArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT)
                .build();

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withHorizontalArrowsNavigation()
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

        YesNoDialog dialog = new YesNoDialog.Builder(
                titleArea,
                contentArea,
                answerArea,
                selectedAnswerArea,
                navigationArea
        )
                .withTheme(theme)
                .withDefaultYesSelected(false)
                .build();

        Optional<Boolean> result = dialog.show();

        if (result.isPresent()) {
            out.println(result.get() ? "Confirmed." : "Declined.");
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
