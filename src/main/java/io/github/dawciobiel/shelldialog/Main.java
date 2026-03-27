package io.github.dawciobiel.shelldialog;

import com.googlecode.lanterna.TextColor;
import io.github.dawciobiel.shelldialog.cli.dialog.SingleChoiceDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;
import io.github.dawciobiel.shelldialog.cli.dialog.option.SimpleDialogOption;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import io.github.dawciobiel.shelldialog.examples.FileExample;
import io.github.dawciobiel.shelldialog.examples.MessageExample;
import io.github.dawciobiel.shelldialog.examples.MultiChoiceExample;
import io.github.dawciobiel.shelldialog.examples.PasswordExample;
import io.github.dawciobiel.shelldialog.examples.ProgressExample;
import io.github.dawciobiel.shelldialog.examples.SingleChoiceExample;
import io.github.dawciobiel.shelldialog.examples.SpinnerExample;
import io.github.dawciobiel.shelldialog.examples.TextLineExample;
import io.github.dawciobiel.shelldialog.examples.YesNoExample;

import java.util.List;
import java.util.Optional;

/**
 * Entry point used for launching bundled example dialogs from the command line.
 */
public class Main {

    private Main() {
    }

    /**
     * Launches one of the example applications.
     *
     * @param args the first argument selects the example name. If empty, an interactive menu is shown.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            runInteractiveLoop();
        } else {
            runFromArgs(args);
        }
    }

    private static void runInteractiveLoop() {
        while (true) {
            SingleChoiceDialog menu = createMainMenu();
            Optional<DialogOption> result = menu.show();

            if (result.isEmpty()) {
                break;
            }

            int code = result.get().getCode();
            if (code == 0) {
                break;
            }

            String[] emptyArgs = new String[0];
            switch (code) {
                case 1 -> SingleChoiceExample.main(emptyArgs);
                case 2 -> MultiChoiceExample.main(emptyArgs);
                case 3 -> TextLineExample.main(emptyArgs);
                case 4 -> PasswordExample.main(emptyArgs);
                case 5 -> YesNoExample.main(emptyArgs);
                case 6 -> FileExample.main(emptyArgs);
                case 7 -> ProgressExample.main(emptyArgs);
                case 8 -> SpinnerExample.main(emptyArgs);
                case 9 -> MessageExample.main(emptyArgs);
            }
        }
    }

    private static SingleChoiceDialog createMainMenu() {
        String version = Version.get();
        String title = "ShellDialog Examples Gallery" + (version.equals("unknown") ? "" : " (v" + version + ")");

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle(title)
                .withTitleColor(TextColor.ANSI.RED_BRIGHT)
                .build();

        ContentArea menuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.WHITE)
                .build();

        ContentArea selectedMenuItemArea = new ContentArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.CYAN)
                .build();

        List<DialogOption> options = List.of(
                new SimpleDialogOption(1, "Single Choice Dialog"),
                new SimpleDialogOption(2, "Multi Choice Dialog"),
                new SimpleDialogOption(3, "Text Line Dialog"),
                new SimpleDialogOption(4, "Password Dialog"),
                new SimpleDialogOption(5, "Yes/No Dialog"),
                new SimpleDialogOption(6, "File Selection Dialog"),
                new SimpleDialogOption(7, "Progress Dialog"),
                new SimpleDialogOption(8, "Spinner Dialog (Indeterminate)"),
                new SimpleDialogOption(9, "Message Dialog (Info/Alert)"),
                new SimpleDialogOption(0, "Exit")
        );

        DialogTheme theme = DialogTheme.builder()
                                       .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                                       .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                                       .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
                                       .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                                       .validationMessageStyle(TextStyle.of(TextColor.ANSI.RED_BRIGHT, TextColor.ANSI.DEFAULT))
                                       .build();


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
                                TextColor.ANSI.RED_BRIGHT,
                                TextColor.ANSI.WHITE,
                                TextColor.ANSI.DEFAULT
                        )
                )
                .withTheme(theme)
                .build();

        return new SingleChoiceDialog.Builder(
                titleArea,
                menuItemArea,
                selectedMenuItemArea,
                options,
                navigationArea
        )
                .withTheme(theme)
                .withBorderColor(TextColor.ANSI.RED)
                .build();
    }

    private static void runFromArgs(String[] args) {
        String arg = args[0];

        switch (arg.toLowerCase()) {
            case "--version", "-v", "version" -> System.out.println(Version.get());
            case "multichoice" -> MultiChoiceExample.main(args);
            case "password" -> PasswordExample.main(args);
            case "singlechoice" -> SingleChoiceExample.main(args);
            case "textline" -> TextLineExample.main(args);
            case "yesno" -> YesNoExample.main(args);
            case "file" -> FileExample.main(args);
            case "progress" -> ProgressExample.main(args);
            case "spinner" -> SpinnerExample.main(args);
            case "message" -> MessageExample.main(args);

            default -> {
                System.out.println("Unknown dialog example: [" + arg + "]");
                System.out.println("Possible dialog examples:");
                System.out.println("singlechoice, multichoice, textline, password, yesno, file, progress, spinner, message, version");
            }
        }
    }
}
