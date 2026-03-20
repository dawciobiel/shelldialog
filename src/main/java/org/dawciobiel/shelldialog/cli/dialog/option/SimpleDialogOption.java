package org.dawciobiel.shelldialog.cli.dialog.option;

public class SimpleDialogOption implements DialogOption {
    private final int code;
    private final String label;

    public SimpleDialogOption(int code, String label) {
        this.code = code;
        this.label = label;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
