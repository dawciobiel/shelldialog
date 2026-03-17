package org.dawciobiel.shelldialog.cli.style;

import com.googlecode.lanterna.TextColor;

public final class DialogTheme {

    private final TextStyle borderStyle;
    private final TextStyle titleStyle;
    private final TextStyle inputStyle;
    private final TextStyle navigationStyle;

    private DialogTheme(Builder builder) {
        this.borderStyle = builder.borderStyle;
        this.titleStyle = builder.titleStyle;
        this.inputStyle = builder.inputStyle;
        this.navigationStyle = builder.navigationStyle;
    }

    public TextStyle borderStyle() {
        return borderStyle;
    }

    public TextStyle titleStyle() {
        return titleStyle;
    }

    public TextStyle inputStyle() {
        return inputStyle;
    }

    public TextStyle navigationStyle() {
        return navigationStyle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static DialogTheme darkTheme() {
        return builder()
                .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
                .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
                .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
                .navigationStyle(TextStyle.of(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
                .build();
    }

    public static final class Builder {
        private TextStyle borderStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle titleStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle inputStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);
        private TextStyle navigationStyle = TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT);

        public Builder borderStyle(TextStyle style) {
            this.borderStyle = style;
            return this;
        }

        public Builder titleStyle(TextStyle style) {
            this.titleStyle = style;
            return this;
        }

        public Builder inputStyle(TextStyle style) {
            this.inputStyle = style;
            return this;
        }

        public Builder navigationStyle(TextStyle style) {
            this.navigationStyle = style;
            return this;
        }

        public DialogTheme build() {
            return new DialogTheme(this);
        }
    }
}