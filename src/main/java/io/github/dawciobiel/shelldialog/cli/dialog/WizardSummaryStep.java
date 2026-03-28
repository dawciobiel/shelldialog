package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Read-only summary step for {@link WizardDialog}.
 */
public final class WizardSummaryStep implements WizardStep {

    /**
     * Typed summary entry with a label and optional value.
     *
     * @param label summary label
     * @param value summary value, nullable
     */
    public record SummaryItem(String label, String value) {

        /**
         * Creates a summary item.
         *
         * @param label summary label
         * @param value summary value, nullable
         * @return a new summary item
         */
        public static SummaryItem of(String label, String value) {
            String normalizedLabel = Objects.requireNonNull(label).trim();
            if (normalizedLabel.isEmpty()) {
                throw new IllegalArgumentException("label must not be blank");
            }
            return new SummaryItem(normalizedLabel, value);
        }
    }

    private final String title;
    private final String description;
    private final Function<WizardContext, List<String>> linesSupplier;

    private WizardSummaryStep(String title, String description, Function<WizardContext, List<String>> linesSupplier) {
        this.title = title;
        this.description = description;
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
        return of(title, null, linesSupplier);
    }

    /**
     * Creates a summary step with an optional description.
     *
     * @param title step title
     * @param description optional help text shown below the header
     * @param linesSupplier function producing lines from the current context
     * @return a new summary step
     */
    public static WizardSummaryStep of(String title, String description, Function<WizardContext, List<String>> linesSupplier) {
        String normalizedTitle = Objects.requireNonNull(title).trim();
        if (normalizedTitle.isEmpty()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        String normalizedDescription = description == null ? null : description.trim();
        if (normalizedDescription != null && normalizedDescription.isEmpty()) {
            normalizedDescription = null;
        }
        return new WizardSummaryStep(normalizedTitle, normalizedDescription, Objects.requireNonNull(linesSupplier));
    }

    /**
     * Creates a summary step backed by typed summary items.
     *
     * @param title step title
     * @param itemsSupplier function producing summary items from the current context
     * @return a new summary step
     */
    public static WizardSummaryStep keyValues(String title, Function<WizardContext, List<SummaryItem>> itemsSupplier) {
        return keyValues(title, null, itemsSupplier);
    }

    /**
     * Creates a summary step with description and typed summary items.
     *
     * @param title step title
     * @param description optional help text shown below the header
     * @param itemsSupplier function producing summary items from the current context
     * @return a new summary step
     */
    public static WizardSummaryStep keyValues(
            String title,
            String description,
            Function<WizardContext, List<SummaryItem>> itemsSupplier
    ) {
        return of(title, description, context -> formatSummaryItems(Objects.requireNonNull(itemsSupplier).apply(context)));
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(description);
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

    static List<String> formatSummaryItems(List<SummaryItem> items) {
        if (items.isEmpty()) {
            return List.of();
        }

        int labelWidth = items.stream()
                .map(SummaryItem::label)
                .mapToInt(String::length)
                .max()
                .orElse(0);

        List<String> lines = new ArrayList<>(items.size());
        for (SummaryItem item : items) {
            lines.add(item.label() + " ".repeat(labelWidth - item.label().length()) + ": " + normalizedValue(item.value()));
        }
        return lines;
    }

    private static String normalizedValue(String value) {
        if (value == null) {
            return "<not provided>";
        }
        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? "<not provided>" : normalizedValue;
    }
}
