package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Read-only summary step for {@link WizardDialog}.
 */
public final class WizardSummaryStep implements WizardStep {

    private final String title;
    private final Function<WizardContext, List<String>> linesSupplier;

    private WizardSummaryStep(String title, Function<WizardContext, List<String>> linesSupplier) {
        this.title = title;
        this.linesSupplier = linesSupplier;
    }

    /**
     * Creates a summary step.
     *
     * @param title step title
     * @param linesSupplier function producing lines from the current context
     * @return a new summary step
     */
    public static WizardSummaryStep of(String title, Function<WizardContext, List<String>> linesSupplier) {
        String normalizedTitle = Objects.requireNonNull(title).trim();
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        return new WizardSummaryStep(normalizedTitle, Objects.requireNonNull(linesSupplier));
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public int width(WizardContext context) {
        return lines(context).stream().mapToInt(String::length).max().orElse(0);
    }

    @Override
    public int height(WizardContext context) {
        return Math.max(1, lines(context).size());
    }

    @Override
    public void render(TextGraphics tg, int column, int row, WizardContext context, ContentArea contentArea, InputArea inputArea) throws IOException {
        List<String> lines = lines(context);
        if (lines.isEmpty()) {
            contentArea.withContent("").render(tg, column, row);
            return;
        }

        for (int i = 0; i < lines.size(); i++) {
            contentArea.withContent(lines.get(i)).render(tg, column, row + i);
        }
    }

    @Override
    public void handleInput(KeyStroke key) {
    }

    @Override
    public Optional<String> validate() {
        return Optional.empty();
    }

    @Override
    public void commit(WizardContext context) {
    }

    private List<String> lines(WizardContext context) {
        return linesSupplier.apply(context);
    }
}
