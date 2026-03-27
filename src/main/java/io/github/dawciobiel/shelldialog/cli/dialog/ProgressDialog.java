package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A dialog that displays a progress bar while executing a background task.
 */
public class ProgressDialog extends AbstractDialog<Boolean> {

    private final TitleArea titleArea;
    private final ContentArea statusArea;
    private final TextStyle progressBarFilledStyle;
    private final TextStyle progressBarEmptyStyle;
    private final ProgressTask task;
    private final DialogFrame dialogFrame;
    private final boolean borderVisible;

    private static final int BAR_WIDTH = 30;

    private final AtomicReference<ProgressState> state = new AtomicReference<>(new ProgressState(0.0, ""));
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final AtomicReference<Throwable> error = new AtomicReference<>(null);

    private record ProgressState(double progress, String message) {}

    private ProgressDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.statusArea = builder.statusArea;
        this.progressBarFilledStyle = builder.progressBarFilledStyle;
        this.progressBarEmptyStyle = builder.progressBarEmptyStyle;
        this.task = builder.task;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
    }

    @Override
    protected Optional<Boolean> runDialog(Screen screen) throws IOException {
        screen.setCursorPosition(null);
        TextGraphics tg = screen.newTextGraphics();

        Thread taskThread = new Thread(() -> {
            try {
                task.run(new ProgressReporter() {
                    @Override
                    public void update(double progress, String message) {
                        state.set(new ProgressState(Math.clamp(progress, 0.0, 1.0), message != null ? message : ""));
                    }

                    @Override
                    public boolean isCancelled() {
                        return cancelled.get();
                    }
                });
            } catch (Throwable e) {
                error.set(e);
            } finally {
                finished.set(true);
            }
        }, "ProgressTask-Thread");

        taskThread.start();

        try {
            while (!finished.get()) {
                render(screen, tg);

                KeyStroke key = screen.pollInput();
                if (key != null && key.getKeyType() == KeyType.Escape) {
                    cancelled.set(true);
                }

                Thread.sleep(50); // Refresh rate ~20 FPS
            }
            
            // Final render
            render(screen, tg);
            
            if (error.get() != null) {
                return Optional.of(false);
            }

            return cancelled.get() ? Optional.of(false) : Optional.of(true);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.of(false);
        }
    }

    private void render(Screen screen, TextGraphics tg) throws IOException {
        screen.clear();

        ProgressState currentState = state.get();
        String message = currentState.message();
        
        int contentWidth = Math.max(titleArea.getWidth(), Math.max(BAR_WIDTH + 10, message.length()));
        int contentHeight = titleArea.getHeight() + 1 + 1 + 1 + 1; // Title, Spacer, Bar, Spacer, Status

        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int col = layout.contentColumn();
        int row = layout.contentRow();

        titleArea.render(tg, col, row);
        row += titleArea.getHeight();
        row++; // Spacer

        renderProgressBar(tg, col, row, currentState.progress());
        row++;
        row++; // Spacer

        statusArea.withContent(message).render(tg, col, row);

        screen.refresh();
    }

    private void renderProgressBar(TextGraphics tg, int col, int row, double progress) {
        int filledWidth = (int) (progress * BAR_WIDTH);
        
        // Filled part
        tg.setForegroundColor(progressBarFilledStyle.foreground());
        tg.setBackgroundColor(progressBarFilledStyle.background());
        for (int i = 0; i < filledWidth; i++) {
            tg.putString(col + i, row, "█");
        }

        // Empty part
        tg.setForegroundColor(progressBarEmptyStyle.foreground());
        tg.setBackgroundColor(progressBarEmptyStyle.background());
        for (int i = filledWidth; i < BAR_WIDTH; i++) {
            tg.putString(col + i, row, "░");
        }

        // Percentage
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        tg.setBackgroundColor(TextColor.ANSI.DEFAULT);
        tg.putString(col + BAR_WIDTH + 2, row, String.format("%3d%%", (int) (progress * 100)));
    }

    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea statusArea;
        private ProgressTask task;
        private TextStyle progressBarFilledStyle = TextStyle.of(TextColor.ANSI.GREEN, TextColor.ANSI.DEFAULT);
        private TextStyle progressBarEmptyStyle = TextStyle.of(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT);
        private String inputStreamPath = "/dev/tty";
        private String outputStreamPath = "/dev/tty";

        public Builder(TitleArea titleArea, ContentArea statusArea) {
            this.titleArea = Objects.requireNonNull(titleArea);
            this.statusArea = Objects.requireNonNull(statusArea);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder withTask(ProgressTask task) {
            this.task = Objects.requireNonNull(task);
            return this;
        }

        public Builder withProgressBarStyles(TextStyle filled, TextStyle empty) {
            this.progressBarFilledStyle = Objects.requireNonNull(filled);
            this.progressBarEmptyStyle = Objects.requireNonNull(empty);
            return this;
        }

        public Builder inputStream(String path) {
            this.inputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        public Builder outputStream(String path) {
            this.outputStreamPath = Objects.requireNonNull(path);
            return this;
        }

        public ProgressDialog build() {
            if (task == null) {
                throw new IllegalStateException("Task must be provided");
            }
            return new ProgressDialog(this);
        }
    }
}
