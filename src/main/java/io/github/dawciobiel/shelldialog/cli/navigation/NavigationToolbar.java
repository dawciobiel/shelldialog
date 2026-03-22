package io.github.dawciobiel.shelldialog.cli.navigation;

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
         * Appends the standard escape-to-cancel item.
         *
         * @return this builder
         */
        public Builder withEscapeCancel() {
            items.add(new NavigationItem(NavigationLabels.KEY_ESCAPE, NavigationLabels.ACTION_CANCEL));
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
