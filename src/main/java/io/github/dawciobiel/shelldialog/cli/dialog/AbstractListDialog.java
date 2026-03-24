package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.util.List;

/**
 * Base class for dialogs that display a scrollable list of options.
 * Handles viewport calculations and focus navigation.
 *
 * @param <T> the result type of the dialog
 */
public abstract class AbstractListDialog<T> extends AbstractDialog<T> {

    protected List<DialogOption> options;
    protected final int visibleItemCount;

    protected AbstractListDialog(String inputStreamPath, String outputStreamPath,
                                 List<DialogOption> options, int visibleItemCount) {
        super(inputStreamPath, outputStreamPath);
        this.options = options;
        this.visibleItemCount = visibleItemCount;
    }

    protected int initialFocusedIndex() {
        for (int index = 0; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return 0;
    }

    protected int nextEnabledIndex(int currentIndex) {
        for (int index = currentIndex + 1; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    protected int previousEnabledIndex(int currentIndex) {
        for (int index = currentIndex - 1; index >= 0; index--) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    protected int firstVisibleIndex(int focusedIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return 0;
        }
        int maxStartIndex = options.size() - visibleItemCount;
        return Math.min(Math.max(0, focusedIndex - visibleItemCount + 1), maxStartIndex);
    }

    protected int lastVisibleIndex(int firstVisibleIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return options.size();
        }
        return Math.min(options.size(), firstVisibleIndex + visibleItemCount);
    }

    protected boolean hasViewport() {
        return visibleItemCount > 0 && visibleItemCount < options.size();
    }

    protected String positionIndicatorLabel(int focusedIndex) {
        return (focusedIndex + 1) + "/" + options.size();
    }
}
