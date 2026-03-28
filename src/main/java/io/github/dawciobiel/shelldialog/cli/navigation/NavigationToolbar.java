package io.github.dawciobiel.shelldialog.cli.navigation;

import com.googlecode.lanterna.input.KeyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Describes the items and separators shown in a navigation toolbar.
 */
public final class NavigationToolbar {

    private final List<NavigationItem> items;
    private final String itemSeparator;
    private final String hotkeyLabelSeparator;

    private NavigationToolbar(Builder builder) {
        this.items = List.copyOf(builder.items);
        this.itemSeparator = builder.itemSeparator;
        this.hotkeyLabelSeparator = builder.hotkeyLabelSeparator;
    }

    /**
     * Creates a builder for a navigation toolbar.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the toolbar items in render order.
     *
     * @return an immutable list of toolbar items
     */
    public List<NavigationItem> getItems() {
        return items;
    }

    /**
     * Returns the separator inserted between toolbar items.
     *
     * @return the item separator
     */
    public String getItemSeparator() {
        return itemSeparator;
    }

    /**
     * Returns the separator inserted between an item's hotkey and label.
     *
     * @return the hotkey-label separator
     */
    public String getHotkeyLabelSeparator() {
        return hotkeyLabelSeparator;
    }

    /**
     * Builder for {@link NavigationToolbar} instances.
     */
    public static final class Builder {
        private final List<NavigationItem> items = new ArrayList<>();
        private String itemSeparator = NavigationLabels.SEP_ITEM;
        private String hotkeyLabelSeparator = NavigationLabels.SEP_HOTKEYLABEL;

        /**
         * Creates an empty toolbar builder.
         */
        public Builder() {
        }

        /**
         * Appends a custom key and action label to the toolbar.
         *
         * @param key   the key to display (its name will be used)
         * @param label the action description
         * @return this builder
         */
        public Builder withKey(KeyType key, String label) {
            items.add(new NavigationItem(key.name(), label));
            return this;
        }

        /**
         * Appends the standard arrows-navigation item.
         *
         * @return this builder
         */
        public Builder withArrowsNavigation() {
            return withVerticalArrowsNavigation();
        }

        /**
         * Appends the standard vertical-arrows navigation item.
         *
         * @return this builder
         */
        public Builder withVerticalArrowsNavigation() {
            items.add(new NavigationItem(NavigationLabels.KEY_ARROWS_VERTICAL, NavigationLabels.ACTION_NAVIGATION));
            return this;
        }

        /**
         * Appends the standard horizontal-arrows navigation item.
         *
         * @return this builder
         */
        public Builder withHorizontalArrowsNavigation() {
            items.add(new NavigationItem(NavigationLabels.KEY_ARROWS_HORIZONTAL, NavigationLabels.ACTION_NAVIGATION));
            return this;
        }

        /**
         * Appends the standard left-arrow-to-back item.
         *
         * @return this builder
         */
        public Builder withArrowLeftBack() {
            items.add(new NavigationItem(NavigationLabels.KEY_ARROW_LEFT, NavigationLabels.ACTION_BACK));
            return this;
        }

        /**
         * Appends the standard right-arrow-to-next item.
         *
         * @return this builder
         */
        public Builder withArrowRightNext() {
            items.add(new NavigationItem(NavigationLabels.KEY_ARROW_RIGHT, NavigationLabels.ACTION_NEXT));
            return this;
        }

        /**
         * Appends the standard space-to-select item.
         *
         * @return this builder
         */
        public Builder withSpaceSelect() {
            items.add(new NavigationItem(NavigationLabels.KEY_SPACE, NavigationLabels.ACTION_SELECT));
            return this;
        }

        /**
         * Appends the standard enter-to-accept item.
         *
         * @return this builder
         */
        public Builder withEnterAccept() {
            items.add(new NavigationItem(NavigationLabels.KEY_ENTER, NavigationLabels.ACTION_ACCEPT));
            return this;
        }

        /**
         * Appends the standard enter-to-next item.
         *
         * @return this builder
         */
        public Builder withEnterNext() {
            items.add(new NavigationItem(NavigationLabels.KEY_ENTER, NavigationLabels.ACTION_NEXT));
            return this;
        }

        /**
         * Appends the standard enter-to-finish item.
         *
         * @return this builder
         */
        public Builder withEnterFinish() {
            items.add(new NavigationItem(NavigationLabels.KEY_ENTER, NavigationLabels.ACTION_FINISH));
            return this;
        }

        /**
         * Appends the standard enter-to-confirm item (OK).
         *
         * @return this builder
         */
        public Builder withEnterOK() {
            items.add(new NavigationItem(NavigationLabels.KEY_ENTER, NavigationLabels.ACTION_OK));
            return this;
        }

        /**
         * Appends the standard escape-to-cancel item.
         *
         * @return this builder
         */
        public Builder withEscapeCancel() {
            items.add(new NavigationItem(NavigationLabels.KEY_ESCAPE, NavigationLabels.ACTION_CANCEL));
            return this;
        }

        /**
         * Appends the standard tab-to-next-field item.
         *
         * @return this builder
         */
        public Builder withTabNextField() {
            items.add(new NavigationItem(NavigationLabels.KEY_TAB, NavigationLabels.ACTION_NEXT_FIELD));
            return this;
        }

        /**
         * Appends the standard F2-to-toggle-hidden-files item.
         *
         * @return this builder
         */
        public Builder withF2ToggleHiddenFiles() {
            items.add(new NavigationItem(NavigationLabels.KEY_F2, NavigationLabels.ACTION_HIDDEN_FILES));
            return this;
        }

        /**
         * Appends the standard F4-to-cycle-filter item.
         *
         * @return this builder
         */
        public Builder withF4CycleFilter() {
            items.add(new NavigationItem(NavigationLabels.KEY_F4, NavigationLabels.ACTION_CYCLE_FILTER));
            return this;
        }

        /**
         * Appends the standard F7-to-create-folder item.
         *
         * @return this builder
         */
        public Builder withF7NewFolder() {
            items.add(new NavigationItem(NavigationLabels.KEY_F7, NavigationLabels.ACTION_NEW_FOLDER));
            return this;
        }

        /**
         * Appends the standard F5-to-refresh item.
         *
         * @return this builder
         */
        public Builder withF5Refresh() {
            items.add(new NavigationItem(NavigationLabels.KEY_F5, NavigationLabels.ACTION_REFRESH));
            return this;
        }

        /**
         * Appends the standard Home-to-home-dir item.
         *
         * @return this builder
         */
        public Builder withHomeHomeDir() {
            items.add(new NavigationItem(NavigationLabels.KEY_HOME, NavigationLabels.ACTION_HOME));
            return this;
        }

        /**
         * Appends the standard End-to-cwd item.
         *
         * @return this builder
         */
        public Builder withEndCWD() {
            items.add(new NavigationItem(NavigationLabels.KEY_END, NavigationLabels.ACTION_CWD));
            return this;
        }

        /**
         * Sets the separator inserted between toolbar items.
         *
         * @param sep the item separator
         * @return this builder
         */
        public Builder itemSeparator(String sep) {
            this.itemSeparator = Objects.requireNonNull(sep);
            return this;
        }

        /**
         * Sets the separator inserted between an item's hotkey and label.
         *
         * @param sep the hotkey-label separator
         * @return this builder
         */
        public Builder hotkeyLabelSeparator(String sep) {
            this.hotkeyLabelSeparator = Objects.requireNonNull(sep);
            return this;
        }

        /**
         * Builds the toolbar.
         *
         * @return a new {@link NavigationToolbar}
         */
        public NavigationToolbar build() {
            return new NavigationToolbar(this);
        }
    }
}
