package io.github.dawciobiel.shelldialog.cli.style;

import com.googlecode.lanterna.TextColor;

/**
 * Collects the text styles used by the different areas of a dialog.
 */
public final class DialogTheme {

    private final TextStyle borderStyle;
    private final TextStyle titleStyle;
    private final TextStyle contentStyle;
    private final TextStyle inputStyle;
    private final TextStyle navigationStyle;
    private final TextStyle menuItemStyle;
    private final TextStyle menuItemSelectedStyle;

    private DialogTheme(Builder builder) {
        this.borderStyle = builder.borderStyle;
        this.titleStyle = builder.titleStyle;
        this.contentStyle = builder.contentStyle;
        this.inputStyle = builder.inputStyle;
        this.navigationStyle = builder.navigationStyle;
        this.menuItemStyle = builder.menuItemStyle;
        this.menuItemSelectedStyle = builder.menuItemSelectedStyle;
    }

    /**
     * Creates a builder for a custom dialog theme.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates the default built-in dark theme.
     *
     * @return a ready-to-use theme with dark-oriented colors
     */
    public static DialogTheme darkTheme() {
        return builder().borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                        .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                        .contentStyle(TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT))
                        .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                        .navigationStyle(TextStyle.of(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
                        .menuItemStyle(TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT))
                        .menuItemSelectedStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                        .build();
    }

    /**
     * Returns the style used for dialog borders.
     *
     * @return the border style
     */
    public TextStyle borderStyle() {
        return borderStyle;
    }

    /**
     * Returns the style used for dialog titles.
     *
     * @return the title style
     */
    public TextStyle titleStyle() {
        return titleStyle;
    }

    /**
     * Returns the style used for body text.
     *
     * @return the content style
     */
    public TextStyle contentStyle() {
        return contentStyle;
    }

    /**
     * Returns the style used for editable input content.
     *
     * @return the input style
     */
    public TextStyle inputStyle() {
        return inputStyle;
    }

    /**
     * Returns the style used for the navigation toolbar.
     *
     * @return the navigation style
     */
    public TextStyle navigationStyle() {
        return navigationStyle;
    }

    /**
     * Returns the style used for unselected menu items.
     *
     * @return the default menu item style
     */
    public TextStyle menuItemStyle() {
        return menuItemStyle;
    }

    /**
     * Returns the style used for the currently selected menu item.
     *
     * @return the selected menu item style
     */
    public TextStyle menuItemSelectedStyle() {
        return menuItemSelectedStyle;
    }

    /**
     * Builder for {@link DialogTheme} instances.
     */
    public static final class Builder {
        private TextStyle borderStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle titleStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle contentStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle inputStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle navigationStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle menuItemStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle menuItemSelectedStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);

        /**
         * Creates a builder with neutral default styles for every area.
         */
        public Builder() {
        }

        /**
         * Sets the style used for borders.
         *
         * @param style the border style
         * @return this builder
         */
        public Builder borderStyle(TextStyle style) {
            this.borderStyle = style;
            return this;
        }

        /**
         * Sets the style used for titles.
         *
         * @param style the title style
         * @return this builder
         */
        public Builder titleStyle(TextStyle style) {
            this.titleStyle = style;
            return this;
        }

        /**
         * Sets the style used for content text.
         *
         * @param style the content style
         * @return this builder
         */
        public Builder contentStyle(TextStyle style) {
            this.contentStyle = style;
            return this;
        }

        /**
         * Sets the style used for input fields.
         *
         * @param style the input style
         * @return this builder
         */
        public Builder inputStyle(TextStyle style) {
            this.inputStyle = style;
            return this;
        }

        /**
         * Sets the style used for the navigation toolbar.
         *
         * @param style the navigation style
         * @return this builder
         */
        public Builder navigationStyle(TextStyle style) {
            this.navigationStyle = style;
            return this;
        }

        /**
         * Sets the style used for unselected menu items.
         *
         * @param style the menu item style
         * @return this builder
         */
        public Builder menuItemStyle(TextStyle style) {
            this.menuItemStyle = style;
            return this;
        }

        /**
         * Sets the style used for the selected menu item.
         *
         * @param style the selected menu item style
         * @return this builder
         */
        public Builder menuItemSelectedStyle(TextStyle style) {
            this.menuItemSelectedStyle = style;
            return this;
        }

        /**
         * Builds the theme.
         *
         * @return a new {@link DialogTheme}
         */
        public DialogTheme build() {
            return new DialogTheme(this);
        }
    }
}
