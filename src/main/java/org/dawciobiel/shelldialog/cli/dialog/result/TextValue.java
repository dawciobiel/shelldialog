package org.dawciobiel.shelldialog.cli.dialog.result;

public record TextValue(String value) implements Value {

    @Override
    public String getTextValue() {
        return value;
    }
}
