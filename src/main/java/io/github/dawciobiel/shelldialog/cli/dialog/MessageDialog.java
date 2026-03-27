package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.NavigationArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A simple CLI dialog that displays a message and waits for the user to confirm (OK) or cancel.
 */
public class MessageDialog extends AbstractDialog<Boolean> {

    private final TitleArea titleArea;
    private final ContentArea contentArea;
    private final NavigationArea navigationArea;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;

    private MessageDialog(Builder builder) {
        super(builder.inputStream, builder.outputStream, builder.inputStreamPath, builder.outputStreamPath, builder.terminal);
        this.titleArea = builder.titleArea;
        this.contentArea = builder.contentArea;
        this.navigationArea = builder.navigationArea;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Optional<Boolean> runDialog(Screen screen) throws IOException {
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        while (true) {
            render(screen, tg);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();

            if (type == KeyType.Enter) {
                return Optional.of(true);
            } else if (type == KeyType.Escape) {
                return Optional.empty();
            }
        }
    }

    private void render(Screen screen, TextGraphics tg) throws IOException {
        screen.clear();

        int contentWidth = Math.max(
                Math.max(titleArea.getWidth(), contentArea.getWidth()),
                navigationArea.getWidth()
        );
        int contentHeight = titleArea.getHeight()
                + 1 // Spacer
                + contentArea.getHeight()
                + 1 // Spacer
                + navigationArea.getHeight();

        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();
        
        row++; // Spacer
        
        contentArea.render(tg, column, row);
        row += contentArea.getHeight();
        
        row++; // Spacer
        
        navigationArea.render(tg, column, row);

        screen.refresh();
    }

    /**
     * Builder for creating instances of {@link MessageDialog}.
     */
    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea contentArea;
        private final NavigationArea navigationArea;

        /**
         * Creates a new builder with required UI components.
         *
         * @param titleArea      the title area
         * @param contentArea    the message content area
         * @param navigationArea bottom toolbar area
         */
        public Builder(TitleArea titleArea, ContentArea contentArea, NavigationArea navigationArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.contentArea = Objects.requireNonNull(contentArea);
            this.navigationArea = Objects.requireNonNull(navigationArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        /**
         * Builds the {@link MessageDialog} instance.
         *
         * @return a new dialog
         */
        public MessageDialog build() {
            return new MessageDialog(this);
        }
    }
}
