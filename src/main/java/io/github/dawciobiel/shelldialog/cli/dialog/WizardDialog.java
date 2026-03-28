package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbar;
import io.github.dawciobiel.shelldialog.cli.navigation.NavigationToolbarRenderer;
import io.github.dawciobiel.shelldialog.cli.style.DialogTheme;
import io.github.dawciobiel.shelldialog.cli.style.TextStyle;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.DialogFrame;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;
import io.github.dawciobiel.shelldialog.cli.ui.TitleArea;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A multi-step wizard dialog that orchestrates a sequence of {@link WizardStep}s.
 *
 * @param <T> the final result type returned by the wizard
 */
public final class WizardDialog<T> extends AbstractDialog<T> {

    private final String title;
    private final List<WizardStep> steps;
    private final boolean borderVisible;
    private final DialogFrame dialogFrame;
    private final ContentArea contentArea;
    private final InputArea inputArea;
    private final TextStyle validationMessageStyle;
    private final NavigationToolbarRenderer navigationRenderer;
    private final com.googlecode.lanterna.TextColor titleColor;
    private final Function<WizardContext, T> resultMapper;

    private WizardDialog(Builder<T> builder) {
        super(builder.inputStream, builder.outputStream, builder.inputStreamPath, builder.outputStreamPath, builder.terminal);
        this.title = builder.title;
        this.steps = List.copyOf(builder.steps);
        this.borderVisible = builder.borderVisible;
        this.dialogFrame = new DialogFrame(borderVisible, builder.borderStyle);
        this.contentArea = builder.contentArea;
        this.inputArea = builder.inputArea;
        this.validationMessageStyle = builder.validationMessageStyle;
        this.navigationRenderer = builder.navigationRenderer;
        this.titleColor = builder.titleColor;
        this.resultMapper = builder.resultMapper;
    }

    @Override
    protected Optional<T> runDialog(Screen screen) throws IOException {
        TextGraphics tg = screen.newTextGraphics();
        WizardContext context = new WizardContext();
        int currentStepIndex = 0;
        String validationMessage = null;

        while (true) {
            render(screen, tg, context, currentStepIndex, validationMessage);

            KeyStroke key = screen.readInput();
            KeyType type = key.getKeyType();
            WizardStep currentStep = steps.get(currentStepIndex);

            switch (type) {
                case ArrowLeft -> {
                    if (currentStepIndex > 0) {
                        currentStepIndex--;
                        validationMessage = null;
                    }
                }
                case ArrowRight -> {
                    if (currentStepIndex < steps.size() - 1) {
                        Optional<String> validationResult = currentStep.validate();
                        if (validationResult.isPresent()) {
                            validationMessage = validationResult.get();
                        } else {
                            currentStep.commit(context);
                            currentStepIndex++;
                            validationMessage = null;
                        }
                    }
                }
                case Enter -> {
                    Optional<String> validationResult = currentStep.validate();
                    if (validationResult.isPresent()) {
                        validationMessage = validationResult.get();
                        break;
                    }

                    currentStep.commit(context);
                    if (currentStepIndex == steps.size() - 1) {
                        return Optional.of(resultMapper.apply(context));
                    }

                    currentStepIndex++;
                    validationMessage = null;
                }
                case Escape -> {
                    return Optional.empty();
                }
                default -> {
                    currentStep.handleInput(key);
                    validationMessage = null;
                }
            }
        }
    }

    private void render(
            Screen screen,
            TextGraphics tg,
            WizardContext context,
            int currentStepIndex,
            String validationMessage
    ) throws IOException {
        screen.clear();
        screen.setCursorPosition(null);

        WizardStep currentStep = steps.get(currentStepIndex);
        TitleArea titleArea = titleArea(currentStepIndex, currentStep);
        String progressBar = progressBar(currentStepIndex, steps.size());
        NavigationToolbar navigationToolbar = navigationToolbar(currentStepIndex);
        Optional<String> description = currentStep.description();
        int contentWidth = Math.max(
                Math.max(Math.max(titleArea.getWidth(), progressBar.length()), currentStep.width(context)),
                navigationRenderer.measureWidth(navigationToolbar)
        );
        if (description.isPresent()) {
            contentWidth = Math.max(contentWidth, description.get().length());
        }

        if (validationMessage != null) {
            contentWidth = Math.max(contentWidth, validationMessage.length());
        }

        int contentHeight = titleArea.getHeight()
                + 1
                + 1
                + 1
                + currentStep.height(context);
        if (description.isPresent()) {
            contentHeight += 1 + 1;
        }
        if (validationMessage != null) {
            contentHeight += 1 + 1;
        }
        contentHeight += 1 + 1;

        DialogFrame.FrameLayout layout = dialogFrame.layoutFor(contentWidth, contentHeight);
        dialogFrame.render(tg, layout);

        int column = layout.contentColumn();
        int row = layout.contentRow();
        titleArea.render(tg, column, row);
        row += titleArea.getHeight();

        row++;
        contentArea.withContent(progressBar).render(tg, column, row);
        row++;
        row++;
        if (description.isPresent()) {
            contentArea.withContent(description.get()).render(tg, column, row);
            row++;
            row++;
        }

        currentStep.render(tg, column, row, context, contentArea, inputArea);
        row += currentStep.height(context);

        if (validationMessage != null) {
            row++;
            ContentArea validationArea = new ContentArea.Builder()
                    .withContent(validationMessage)
                    .withForegroundColor(validationMessageStyle.foreground())
                    .withBackgroundColor(validationMessageStyle.background())
                    .build();
            validationArea.render(tg, column, row);
            row++;
        }

        row++;
        navigationRenderer.render(tg, navigationToolbar, column, row);

        int contentRow = layout.contentRow() + titleArea.getHeight() + 3;
        if (description.isPresent()) {
            contentRow += 2;
        }
        Optional<TerminalPosition> cursor = currentStep.cursorPosition(column, contentRow, context);
        cursor.ifPresent(screen::setCursorPosition);
        screen.refresh();
    }

    private TitleArea titleArea(int currentStepIndex, WizardStep currentStep) {
        return new TitleArea.Builder()
                .withTitle(title + " - " + currentStep.title() + " (" + (currentStepIndex + 1) + "/" + steps.size() + ")")
                .withTitleColor(titleColor)
                .build();
    }

    static String progressBar(int currentStepIndex, int stepCount) {
        StringBuilder builder = new StringBuilder(stepCount + 2);
        builder.append('[');
        for (int stepIndex = 0; stepIndex < stepCount; stepIndex++) {
            builder.append(stepIndex <= currentStepIndex ? '#' : '-');
        }
        builder.append(']');
        return builder.toString();
    }

    private NavigationToolbar navigationToolbar(int currentStepIndex) {
        NavigationToolbar.Builder builder = NavigationToolbar.builder();
        if (currentStepIndex > 0) {
            builder.withArrowLeftBack();
        }
        if (currentStepIndex < steps.size() - 1) {
            builder.withArrowRightNext().withEnterNext();
        } else {
            builder.withEnterFinish();
        }
        return builder.withEscapeCancel().build();
    }

    /**
     * Builder for {@link WizardDialog}.
     *
     * @param <T> result type returned by the wizard
     */
    public static final class Builder<T> extends AbstractFrameDialogBuilder<Builder<T>> {
        private final String title;
        private final List<WizardStep> steps;
        private ContentArea contentArea = new ContentArea.Builder().withTheme(DialogTheme.darkTheme()).build();
        private InputArea inputArea = new InputArea.Builder().withTheme(DialogTheme.darkTheme()).build();
        private TextStyle validationMessageStyle = DialogTheme.darkTheme().validationMessageStyle();
        private NavigationToolbarRenderer navigationRenderer = new NavigationToolbarRenderer(
                DialogTheme.darkTheme().navigationStyle().foreground(),
                DialogTheme.darkTheme().navigationStyle().foreground(),
                DialogTheme.darkTheme().navigationStyle().background()
        );
        private com.googlecode.lanterna.TextColor titleColor = DialogTheme.darkTheme().titleStyle().foreground();
        private Function<WizardContext, T> resultMapper;

        /**
         * Creates a wizard builder.
         *
         * @param title base wizard title
         * @param steps ordered wizard steps
         */
        public Builder(String title, List<WizardStep> steps) {
            this.title = Objects.requireNonNull(title).trim();
            this.steps = List.copyOf(Objects.requireNonNull(steps));
        }

        @Override
        protected Builder<T> self() {
            return this;
        }

        @Override
        public Builder<T> withTheme(DialogTheme theme) {
            super.withTheme(theme);
            DialogTheme normalizedTheme = Objects.requireNonNull(theme);
            this.contentArea = new ContentArea.Builder().withTheme(normalizedTheme).build();
            this.inputArea = new InputArea.Builder().withTheme(normalizedTheme).build();
            this.validationMessageStyle = normalizedTheme.validationMessageStyle();
            this.navigationRenderer = new NavigationToolbarRenderer(
                    normalizedTheme.navigationStyle().foreground(),
                    normalizedTheme.navigationStyle().foreground(),
                    normalizedTheme.navigationStyle().background()
            );
            this.titleColor = normalizedTheme.titleStyle().foreground();
            return this;
        }

        /**
         * Sets the mapper converting wizard context into the final result.
         *
         * @param resultMapper result mapper
         * @return this builder
         */
        public Builder<T> withResultMapper(Function<WizardContext, T> resultMapper) {
            this.resultMapper = Objects.requireNonNull(resultMapper);
            return this;
        }

        /**
         * Builds the wizard dialog.
         *
         * @return a new {@link WizardDialog}
         */
        public WizardDialog<T> build() {
            if (title.isEmpty()) {
                throw new IllegalArgumentException("title must not be blank");
            }
            if (steps.isEmpty()) {
                throw new IllegalArgumentException("steps must not be empty");
            }
            if (resultMapper == null) {
                throw new IllegalStateException("resultMapper must be provided");
            }
            return new WizardDialog<>(this);
        }
    }
}
