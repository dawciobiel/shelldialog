package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.terminal.Terminal;
import io.github.dawciobiel.shelldialog.cli.dialog.option.DialogOption;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for dialogs that display a scrollable and filterable list of options.
 * Handles viewport calculations, focus navigation, and real-time filtering.
 *
 * @param <T> the result type of the dialog
 */
public abstract class AbstractListDialog<T> extends AbstractDialog<T> {

    /**
     * Currently displayed (filtered) options.
     */
    protected List<DialogOption> options;

    /**
     * Full list of options available before filtering.
     */
    protected List<DialogOption> allOptions;

    /**
     * Maximum number of items visible in the viewport.
     */
    protected final int visibleItemCount;

    /**
     * Current search filter text.
     */
    protected String filterText = "";

    protected AbstractListDialog(InputStream in, OutputStream out, String inPath, String outPath, Terminal terminal,
                                 List<DialogOption> options, int visibleItemCount) {
        super(in, out, inPath, outPath, terminal);
        this.allOptions = new ArrayList<>(options);
        this.options = new ArrayList<>(allOptions);
        this.visibleItemCount = visibleItemCount;
    }

    /**
     * Updates the active search filter and refreshes the visible options list.
     *
     * @param newFilterText the new text to filter by (case-insensitive)
     */
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

    /**
     * Finds the first enabled option to focus initially.
     *
     * @return the index of the first enabled option, or 0 if none found
     */
    protected int initialFocusedIndex() {
        for (int index = 0; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return 0;
    }

    /**
     * Calculates the next enabled option index when moving down.
     *
     * @param currentIndex the current focus position
     * @return the index of the next enabled option, or the current index if no more options
     */
    protected int nextEnabledIndex(int currentIndex) {
        if (options.isEmpty()) return -1;
        for (int index = currentIndex + 1; index < options.size(); index++) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    /**
     * Calculates the previous enabled option index when moving up.
     *
     * @param currentIndex the current focus position
     * @return the index of the previous enabled option, or the current index if no more options
     */
    protected int previousEnabledIndex(int currentIndex) {
        if (options.isEmpty()) return -1;
        for (int index = currentIndex - 1; index >= 0; index--) {
            if (options.get(index).isEnabled()) {
                return index;
            }
        }
        return currentIndex;
    }

    /**
     * Calculates the index of the first item that should be visible in the viewport.
     *
     * @param focusedIndex the currently focused item index
     * @return the starting index for rendering the list
     */
    protected int firstVisibleIndex(int focusedIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return 0;
        }
        int maxStartIndex = options.size() - visibleItemCount;
        return Math.clamp(focusedIndex - visibleItemCount + 1, 0, maxStartIndex);
    }

    /**
     * Calculates the index of the last item that should be visible in the viewport.
     *
     * @param firstVisibleIndex the result of {@link #firstVisibleIndex(int)}
     * @return the end index (exclusive) for rendering the list
     */
    protected int lastVisibleIndex(int firstVisibleIndex) {
        if (visibleItemCount <= 0 || visibleItemCount >= options.size()) {
            return options.size();
        }
        return Math.min(options.size(), firstVisibleIndex + visibleItemCount);
    }

    /**
     * Checks if the list is currently being scrolled via a viewport.
     *
     * @return {@code true} if only a subset of items is shown
     */
    protected boolean hasViewport() {
        return visibleItemCount > 0 && visibleItemCount < options.size();
    }

    /**
     * Returns a string describing the current position (e.g., "1/10").
     *
     * @param focusedIndex the currently focused index
     * @return the formatted position label
     */
    protected String positionIndicatorLabel(int focusedIndex) {
        if (options.isEmpty()) return "0/0";
        return (focusedIndex + 1) + "/" + options.size();
    }

    /**
     * Clears current search filter and shows all options.
     */
    protected void clearFilter() {
        updateFilter("");
    }
}
