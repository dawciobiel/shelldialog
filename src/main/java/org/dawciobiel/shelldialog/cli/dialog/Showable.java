package org.dawciobiel.shelldialog.cli.dialog;

import java.util.Optional;

public interface Showable<T> {

    Optional<T> show();
}
