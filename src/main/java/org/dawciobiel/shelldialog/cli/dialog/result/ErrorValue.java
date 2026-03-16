package org.dawciobiel.shelldialog.cli.dialog.result;

public record ErrorValue(String message) implements Value {

    @Override
    public String getErrorValue() {
        return message;
    }
}