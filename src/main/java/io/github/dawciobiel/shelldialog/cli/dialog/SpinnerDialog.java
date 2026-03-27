package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A dialog that displays an animated spinner while executing a background task with indeterminate duration.
 */
public class SpinnerDialog extends AbstractDialog<Boolean> {

    private final TitleArea titleArea;
    private final ContentArea statusArea;
    private final ProgressTask task;
    private final DialogFrame dialogFrame;
    private final boolean borderVisible;
    private final List<String> spinnerFrames;

    private final AtomicReference<String> currentStatus = new AtomicReference<>("");
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    private final AtomicBoolean finished = new AtomicBoolean(false);
    private final AtomicReference<Throwable> error = new AtomicReference<>(null);

    private static final List<String> DEFAULT_SPINNER = List.of("|", "/", "-", "\\");

    private SpinnerDialog(Builder builder) {
        super(builder.inputStreamPath, builder.outputStreamPath);
        this.titleArea = builder.titleArea;
        this.statusArea = builder.statusArea;
        this.task = builder.task;
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.spinnerFrames = builder.spinnerFrames;
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
                        currentStatus.set(message != null ? message : "");
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
        }, "SpinnerTask-Thread");

        taskThread.start();

        int frameIndex = 0;
        try {
            while (!finished.get()) {
                render(screen, tg, spinnerFrames.get(frameIndex));
                frameIndex = (frameIndex + 1) % spinnerFrames.size();

                KeyStroke key = screen.pollInput();
                if (key != null && key.getKeyType() == KeyType.Escape) {
                    cancelled.set(true);
                }

                Thread.sleep(100); // 10 FPS for animation
            }

            if (error.get() != null) {
                return Optional.of(false);
            }

            return cancelled.get() ? Optional.of(false) : Optional.of(true);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.of(false);
        }
    }

    private void render(Screen screen, TextGraphics tg, String spinnerFrame) throws IOException {
        screen.clear();

        String message = currentStatus.get();
        int contentWidth = Math.max(titleArea.getWidth(), Math.max(message.length() + 4, 20));
        int contentHeight = titleArea.getHeight() + 1 + 1 + 1; // Title, Spacer, Spinner+Status

        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int col = layout.contentColumn();
        int row = layout.contentRow();

        titleArea.render(tg, col, row);
        row += titleArea.getHeight();
        row++; // Spacer

        // Render spinner and status
        tg.setForegroundColor(TextColor.ANSI.YELLOW_BRIGHT);
        tg.putString(col, row, spinnerFrame);
        
        tg.setForegroundColor(TextColor.ANSI.DEFAULT);
        statusArea.withContent(message).render(tg, col + 2, row);

        screen.refresh();
    }

    public static class Builder extends AbstractFrameDialogBuilder<Builder> {
        private final TitleArea titleArea;
        private final ContentArea statusArea;
        private ProgressTask task;
        private List<String> spinnerFrames = DEFAULT_SPINNER;
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

        public Builder withSpinnerFrames(List<String> frames) {
            if (frames == null || frames.isEmpty()) {
                throw new IllegalArgumentException("Spinner frames cannot be empty");
            }
            this.spinnerFrames = List.copyOf(frames);
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

        public SpinnerDialog build() {
            if (task == null) {
                throw new IllegalStateException("Task must be provided");
            }
            return new SpinnerDialog(this);
        }
    }
}
