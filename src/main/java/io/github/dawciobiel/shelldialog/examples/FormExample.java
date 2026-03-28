package io.github.dawciobiel.shelldialog.examples;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import io.github.dawciobiel.shelldialog.cli.dialog.FormDialog;
import io.github.dawciobiel.shelldialog.cli.dialog.FormField;
import io.github.dawciobiel.shelldialog.cli.dialog.FormValues;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;
import io.github.dawciobiel.shelldialog.cli.validation.InputValidator;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

/**
 * Demonstrates how to build and show a {@link FormDialog}.
 */
public class FormExample {

    private record AccountData(String username, String email, char[] password) {
    }

    private FormExample() {
    }

    /**
     * Runs the form dialog example.
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
                .build();

        TitleArea titleArea = new TitleArea.Builder()
                .withTitle("Create account")
                .withTheme(theme)
                .build();

        ContentArea contentArea = new ContentArea.Builder()
                .withContent("Fill in all fields. Use Tab or arrows to move between inputs.")
                .withTheme(theme)
                .build();

        ContentArea labelArea = new ContentArea.Builder()
                .withTheme(theme)
                .build();

        InputArea inputArea = new InputArea.Builder()
                .withTheme(theme)
                .build();

        InputArea focusedInputArea = new InputArea.Builder()
                .withForegroundColor(TextColor.ANSI.BLACK)
                .withBackgroundColor(TextColor.ANSI.CYAN)
                .build();

        NavigationArea navigationArea = new NavigationArea.Builder()
                .withToolbar(
                        NavigationToolbar.builder()
                                .withArrowsNavigation()
                                .withKey(KeyType.Tab, "Next Field")
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

        List<FormField> fields = List.of(
                FormField.text("username", "Username")
                        .withInitialValue("dawciobiel")
                        .withMaxLength(20)
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Username is required."))
                        .build(),
                FormField.text("email", "Email")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Email is required.")
                                .and(InputValidator.BuiltIn.email("Enter a valid email address.")))
                        .build(),
                FormField.password("password", "Password")
                        .withMaxLength(32)
                        .withValidator(chars -> chars.length < 6
                                ? Optional.of("Password must contain at least 6 characters.")
                                : Optional.empty())
                        .build()
        );

        FormDialog<AccountData> dialog = new FormDialog.Builder<AccountData>(
                titleArea,
                contentArea,
                labelArea,
                inputArea,
                focusedInputArea,
                fields,
                navigationArea
        )
                .withTheme(theme)
                .withResultMapper(FormExample::toAccountData)
                .build();

        Optional<AccountData> result = dialog.show();
        if (result.isEmpty()) {
            out.println("Dialog cancelled.");
            return;
        }

        AccountData values = result.get();
        out.println("Username: " + values.username());
        out.println("Email: " + values.email());
        out.println("Password length: " + values.password().length);
    }

    private static AccountData toAccountData(FormValues values) {
        return new AccountData(
                values.getString("username"),
                values.getString("email"),
                values.getPassword("password")
        );
    }
}
