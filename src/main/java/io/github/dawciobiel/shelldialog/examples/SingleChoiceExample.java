package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.SingleChoiceDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link SingleChoiceDialog}.
 */
public class SingleChoiceExample {

    private SingleChoiceExample() {
    }

    /**
     * Runs the single-choice dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Select your favorite fruit:")
                .withTitleColor(TextColor.ANSI.YELLOW_BRIGHT)
                .build();

        ContentArea menuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.CYAN)
                .withBackgroundColor(TextColor.ANSI.DEFAULT)
                .build();

        ContentArea selectedMenuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT)
                .build();

        List<DialogOption> options = List.of(
                new SimpleDialogOption(1, "Apple"),
                new SimpleDialogOption(2, "Banana", false),
                new SimpleDialogOption(3, "Cherry"),
                new SimpleDialogOption(4, "Date"),
                new SimpleDialogOption(5, "Elderberry")
        );

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                         .withArrowsNavigation()
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

        SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(
                titleArea,
                menuItemArea,
                selectedMenuItemArea,
                options,
                navigationArea
        )
                .withBorderColor(TextColor.ANSI.BLUE)
                .withVisibleItemCount(3)
                .build();

        Optional<DialogOption> result = dialog.show();

        if (result.isPresent()) {
            DialogOption selected = result.get();
            out.println("You selected: " + selected.getLabel() + " (Code: " + selected.getCode() + ")");
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
