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
        private String itemSeparator = NavigationLabels.SEP_ITEM;
        private String hotkeyLabelSeparator = NavigationLabels.SEP_HOTKEYLABEL;

        public Builder withArrowsNavigation() {
            items.add(new NavigationItem(NavigationLabels.KEY_ARROWS, NavigationLabels.ACTION_NAVIGATION));
            return this;
        }

        public Builder withEnterAccept() {
            items.add(new NavigationItem(NavigationLabels.KEY_ENTER, NavigationLabels.ACTION_ACCEPT));
            return this;
        }

        public Builder withEscapeCancel() {
            items.add(new NavigationItem(NavigationLabels.KEY_ESCAPE, NavigationLabels.ACTION_CANCEL));
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