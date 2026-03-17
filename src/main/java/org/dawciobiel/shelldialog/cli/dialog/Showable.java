package org.dawciobiel.shelldialog.cli.dialog;

import org.dawciobiel.shelldialog.cli.dialog.result.Value;

public interface Showable {

    String DIALOG_CANCELED_FLAG = "[DIALOG_CANCELED]"; // Text value result as flag identifier

    Value show();

}
