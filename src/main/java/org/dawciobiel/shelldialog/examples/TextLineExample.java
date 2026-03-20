package org.dawciobiel.shelldialog.examples;

import org.dawciobiel.shelldialog.cli.dialog.TextLineDialog;

import java.util.Optional;

import static java.lang.System.*;

public class TextLineExample {

    public static void main(String[] args) {
        TextLineDialog dialog = new TextLineDialog.Builder("Please enter your name:")
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
