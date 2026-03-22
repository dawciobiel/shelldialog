package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.MultiChoiceDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link MultiChoiceDialog}.
 */
public class MultiChoiceExample {

    private MultiChoiceExample() {
    }

    /**
     * Runs the multi-choice dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        DialogTheme theme = DialogTheme.darkTheme();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Select your favorite fruits:")
                .withTheme(theme)
                .build();

        ContentArea menuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.CYAN)
                .withBackgroundColor(TextColor.ANSI.DEFAULT)
                .build();

        ContentArea focusedMenuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT)
                .build();

        ContentArea selectedMenuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT)
                .build();

        ContentArea selectedFocusedMenuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.WHITE)
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
                                .withVerticalArrowsNavigation()
                                .withSpaceSelect()
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

        MultiChoiceDialog dialog = new MultiChoiceDialog.Builder(
                titleArea,
                menuItemArea,
                focusedMenuItemArea,
                selectedMenuItemArea,
                selectedFocusedMenuItemArea,
                options,
                navigationArea
        )
                .withTheme(theme)
                .withInitiallySelectedOptions(List.of(
                        options.get(0),
                        options.get(3)
                ))
                .withVisibleItemCount(3)
                .build();

        Optional<List<DialogOption>> result = dialog.show();

        if (result.isPresent()) {
            List<DialogOption> selected = result.get();
            String labels = selected.stream()
                    .map(DialogOption::getLabel)
                    .collect(Collectors.joining(", "));
            out.println(labels.isEmpty() ? "No fruits selected." : "You selected: " + labels);
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
