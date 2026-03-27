package io.github.dawciobiel.shelldialog.cli.dialog;

import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for dialogs that display a scrollable list of options.
 * Handles viewport calculations, focus navigation, and live filtering.
 *
 * @param <T> the result type of the dialog
 */
public abstract class AbstractListDialog<T> extends AbstractDialog<T> {

    protected List<DialogOption> options;
    protected List<DialogOption> allOptions;
    protected final int visibleItemCount;
    protected String filterText = "";

    protected AbstractListDialog(String inputStreamPath, String outputStreamPath,
                                 List<DialogOption> options, int visibleItemCount) {
        super(inputStreamPath, outputStreamPath);
        this.allOptions = new ArrayList<>(options);
        this.options = new ArrayList<>(allOptions);
        this.visibleItemCount = visibleItemCount;
    }

    protected void updateFilter(String newFilterText) {
        this.filterText = newFilterText;
        if (filterText.isEmpty()) {
            this.options = new ArrayList<>(allOptions);
        } else {
            String lowerFilter = filterText.toLowerCase();
            this.options = allOptions.stream()
                    .filter(option -> option.getLabel().toLowerCase().contains(lowerFilter))
                    .toList();
        }
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
        if (options.isEmpty()) return -1;
        for (int index = currentIndex + 1; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    protected int previousEnabledIndex(int currentIndex) {
        if (options.isEmpty()) return -1;
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
        if (options.isEmpty()) return "0/0";
        return (focusedIndex + 1) + "/" + options.size();
    }

    /**
     * Clears current search filter.
     */
    protected void clearFilter() {
        updateFilter("");
    }
}
