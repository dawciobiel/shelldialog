package org.dawciobiel.shelldialog.cli.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NavigationToolbar {

    private final List<NavigationItem> items;
    private final String itemSeparator;
    private final String hotkeyLabelSeparator;

    private NavigationToolbar(Builder builder) {
        this.items = List.copyOf(builder.items);
        this.itemSeparator = builder.itemSeparator;
        this.hotkeyLabelSeparator = builder.hotkeyLabelSeparator;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<NavigationItem> getItems() {
        return items;
    }

    public String getItemSeparator() {
        return itemSeparator;
    }

    public String getHotkeyLabelSeparator() {
        return hotkeyLabelSeparator;
    }

    public static final class Builder {
        private final List<NavigationItem> items = new ArrayList<>();
        private String itemSeparator = NavigationLabels.SEPARATOR_ITEM;
        private String hotkeyLabelSeparator = NavigationLabels.SEPARATOR_HOTKEYLABEL;

        public Builder withArrowsNavigation() {
            items.add(new NavigationItem(NavigationLabels.ARROWS, NavigationLabels.NAVIGATION_TEXT));
            return this;
        }

        public Builder withEnterAccept() {
            items.add(new NavigationItem(NavigationLabels.ENTER, NavigationLabels.ACCEPT));
            return this;
        }

        public Builder withEscapeCancel() {
            items.add(new NavigationItem(NavigationLabels.ESC, NavigationLabels.CANCEL));
            return this;
        }

        public Builder itemSeparator(String sep) {
            this.itemSeparator = Objects.requireNonNull(sep);
            return this;
        }

        public Builder hotkeyLabelSeparator(String sep) {
            this.hotkeyLabelSeparator = Objects.requireNonNull(sep);
            return this;
        }

        public NavigationToolbar build() {
            return new NavigationToolbar(this);
        }
    }
}