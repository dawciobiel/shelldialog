package org.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.dawciobiel.shelldialog.cli.i18n.Messages;
import org.dawciobiel.shelldialog.cli.style.DialogTheme;
import org.dawciobiel.shelldialog.cli.style.TextStyle;
import org.dawciobiel.shelldialog.cli.ui.ContentArea;
import org.dawciobiel.shelldialog.cli.ui.DialogFrame;
import org.dawciobiel.shelldialog.cli.ui.NavigationArea;
import org.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A CLI dialog that returns a boolean yes-or-no answer.
 * It renders two horizontal choices inside the shared optional dialog frame.
 */
public class YesNoDialog extends AbstractDialog<Boolean> {

    private static final String DEFAULT_YES_LABEL = Messages.getString("dialog.answer_yes");
    private static final String DEFAULT_NO_LABEL = Messages.getString("dialog.answer_no");
    private static final int ANSWER_GAP = 3;

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final ContentArea answerArea;
    private final ContentArea selectedAnswerArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final String yesLabel;
    private final String noLabel;

    private YesNoDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.answerArea = builder.answerArea;
        this.selectedAnswerArea = builder.selectedAnswerArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.yesLabel = builder.yesLabel;
        this.noLabel = builder.noLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<Boolean> runDialog(Screen screen) throws IOException {
        boolean yesSelected = true;
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg, yesSelected);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            switch (type) {
                case ArrowLeft -> yesSelected = true;
                case ArrowRight -> yesSelected = false;
                case Enter -> {
                    return Optional.of(yesSelected);
                }
                case Escape -> {
                    return Optional.empty();
                }
                default -> {
                }
            }
        }
    }

    private void render(Screen screen, TextGraphics tg, boolean yesSelected) throws IOException {
        screen.clear();

        String yesText = decorateChoice(yesLabel);
        String noText = decorateChoice(noLabel);
        int answersWidth = yesText.length() + ANSWER_GAP + noText.length();
        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), contentArea.getWidth()),
                Math.max(answersWidth, navigationArea.getWidth())
        );
        int contentHeight = titleArea.getHeight()
                + 1
                + contentArea.getHeight()
                + 1
                + 1
                + 1
                + navigationArea.getHeight();
        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();

        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++;

        contentArea.render(tg, column, row);
        row += contentArea.getHeight();

        row++;

        renderChoices(tg, column, row, yesText, noText, yesSelected);
        row++;

        row++;

        navigationArea.render(tg, column, row);
        screen.refresh();
    }

    private void renderChoices(TextGraphics tg, int column, int row, String yesText, String noText, boolean yesSelected)
            throws IOException {
        ContentArea yesArea = (yesSelected ? selectedAnswerArea : answerArea).withContent(yesText);
        ContentArea noArea = (yesSelected ? answerArea : selectedAnswerArea).withContent(noText);

        yesArea.render(tg, column, row);
        noArea.render(tg, column + yesText.length() + ANSWER_GAP, row);
    }

    private String decorateChoice(String label) {
        return "[" + label + "]";
    }

    /**
     * Builder for creating instances of {@link YesNoDialog}.
     */
    public static class Builder {

        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final ContentArea answerArea;
        private final ContentArea selectedAnswerArea;
        private final NavigationArea navigationArea;

        private boolean borderVisible = true;
        private TextStyle borderStyle = TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT);
        private String yesLabel = DEFAULT_YES_LABEL;
        private String noLabel = DEFAULT_NO_LABEL;
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        /**
         * Creates a new builder with the specified UI areas.
         *
         * @param titleArea the title area to render
         * @param contentArea the body content area to render
         * @param answerArea the style used for unselected answers
         * @param selectedAnswerArea the style used for the selected answer
         * @param navigationArea the navigation area to render
         */
        public Builder(
                TitleArea titleArea,
                ContentArea contentArea,
                ContentArea answerArea,
                ContentArea selectedAnswerArea,
                NavigationArea navigationArea
        ) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.contentArea = Objects.requireNonNull(contentArea);
            this.answerArea = Objects.requireNonNull(answerArea);
            this.selectedAnswerArea = Objects.requireNonNull(selectedAnswerArea);
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        /**
         * Enables or disables the shared dialog border.
         *
         * @param visible {@code true} to render the border, {@code false} to omit it
         * @return this builder
         */
        public Builder withBorder(boolean visible) {
            this.borderVisible = visible;
            return this;
        }

        /**
         * Sets the style used for the shared dialog border.
         *
         * @param style the border style
         * @return this builder
         */
        public Builder withBorderStyle(TextStyle style) {
            this.borderStyle = Objects.requireNonNull(style);
            return this;
        }

        /**
         * Sets the foreground color used for the shared dialog border.
         *
         * @param color the border foreground color
         * @return this builder
         */
        public Builder withBorderColor(TextColor color) {
            this.borderStyle = TextStyle.of(Objects.requireNonNull(color), borderStyle.background());
            return this;
        }

        /**
         * Applies the dialog border style from the provided theme.
         *
         * @param theme the theme supplying the border style
         * @return this builder
         */
        public Builder withTheme(DialogTheme theme) {
            this.borderStyle = Objects.requireNonNull(theme).borderStyle();
            return this;
        }

        /**
         * Sets the label shown for the affirmative answer.
         *
         * @param label the yes label
         * @return this builder
         */
        public Builder withYesLabel(String label) {
            this.yesLabel = Objects.requireNonNull(label);
            return this;
        }

        /**
         * Sets the label shown for the negative answer.
         *
         * @param label the no label
         * @return this builder
         */
        public Builder withNoLabel(String label) {
            this.noLabel = Objects.requireNonNull(label);
            return this;
        }

        /**
         * Sets the input stream path (e.g. {@code /dev/tty}).
         *
         * @param path the path to the input stream
         * @return this builder
         */
        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Sets the output stream path (e.g. {@code /dev/tty}).
         *
         * @param path the path to the output stream
         * @return this builder
         */
        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        /**
         * Builds the dialog instance.
         *
         * @return a new {@link YesNoDialog}
         */
        public YesNoDialog build() {
            return new YesNoDialog(this);
        }
    }
}
