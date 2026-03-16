package org.dawciobiel.shelldialog.cli.dialog.result;

public record IntegerValue(Integer value) implements Value {

    @Override
    public Integer getIntegerValue() {
        return value;
    }
}
