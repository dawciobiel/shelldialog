package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import io.github.dawciobiel.shelldialog.cli.dialog.FileDialog;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link FileDialog}.
 */
public class FileExample {

    private FileExample() {
    }

    /**
     * Runs the file dialog example.
     *
     * @param args command-line arguments, currently ignored
     */
    public static void main(String[] args) {
        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Select a source file:")
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

        DialogTheme theme = DialogTheme.darkTheme();

        String userHome = System.getProperty("user.home");
        Map<KeyType, Path> customShortcuts = Map.of(
                KeyType.F1, Paths.get(userHome, "Desktop"),
                KeyType.F3, Paths.get(userHome, "Documents")
        );

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withArrowsNavigation()
                                .withKey(KeyType.F1, "Desktop")
                                .withKey(KeyType.F3, "Docs")
                                .withF2ToggleHiddenFiles()
                                .withF7NewFolder()
                                .withF5Refresh()
                                .withHomeHomeDir()
                                .withEndCWD()
                                .withEnterAccept()
                                .withEscapeCancel()
                                .build()
                )
                .withTheme(theme)
                .build();

        FileDialog dialog = new FileDialog.Builder(
                titleArea,
                menuItemArea,
                selectedMenuItemArea,
                navigationArea
        )
                .withBorderColor(TextColor.ANSI.BLUE)
                .withVisibleItemCount(10)
                .withTheme(theme)
                .withExtensions(List.of("java", "md", "txt"))
                .withShortcuts(customShortcuts)
                .build();

        Optional<Path> result = dialog.show();

        if (result.isPresent()) {
            out.println("You selected: " + result.get().toAbsolutePath());
        } else {
            out.println("Dialog cancelled.");
        }
    }
}
