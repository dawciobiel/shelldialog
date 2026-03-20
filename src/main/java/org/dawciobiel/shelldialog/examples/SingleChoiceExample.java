package org.dawciobiel.shelldialog.examples;

import org.dawciobiel.shelldialog.cli.dialog.SingleChoiceDialog;
import org.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.util.Optional;

import static java.lang.System.*;

public class SingleChoiceExample {

    public static void main(String[] args) {
        String[] options = {
                "Select your favorite fruit:", // Title
                "Apple",
                "Banana",
                "Cherry",
                "Date",
                "Elderberry"
        };

        SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(options)
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
