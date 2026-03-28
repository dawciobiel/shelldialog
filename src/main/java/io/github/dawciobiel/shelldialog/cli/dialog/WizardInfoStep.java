package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Read-only informational step for {@link WizardDialog}.
 */
public final class WizardInfoStep implements WizardStep {

    private final String title;
    private final String description;
    private final List<String> lines;

    private WizardInfoStep(String title, String description, List<String> lines) {
        this.title = title;
        this.description = description;
        this.lines = List.copyOf(lines);
    }

    /**
     * Creates an informational step.
     *
     * @param title step title
     * @param lines static lines rendered in the step body
     * @return a new info step
     */
    public static WizardInfoStep of(String title, List<String> lines) {
        return of(title, null, lines);
    }

    /**
     * Creates an informational step with an optional description.
     *
     * @param title step title
     * @param description optional help text shown below the header
     * @param lines static lines rendered in the step body
     * @return a new info step
     */
    public static WizardInfoStep of(String title, String description, List<String> lines) {
        String normalizedTitle = normalize(title, "title");
        String normalizedDescription = description == null ? null : description.trim();
        if (normalizedDescription != null && normalizedDescription.isEmpty()) {
            normalizedDescription = null;
        }
        List<String> normalizedLines = List.copyOf(Objects.requireNonNull(lines));
        return new WizardInfoStep(normalizedTitle, normalizedDescription, normalizedLines);
    }

    /**
     * Creates an informational step with bullet-formatted body lines.
     *
     * @param title step title
     * @param lines static bullet lines rendered in the step body
     * @return a new info step
     */
    public static WizardInfoStep bullets(String title, List<String> lines) {
        return bullets(title, null, lines);
    }

    /**
     * Creates an informational step with description and bullet-formatted body lines.
     *
     * @param title step title
     * @param description optional help text shown below the header
     * @param lines static bullet lines rendered in the step body
     * @return a new info step
     */
    public static WizardInfoStep bullets(String title, String description, List<String> lines) {
        return of(title, description, formatBulletLines(lines));
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
        return lines.stream().mapToInt(String::length).max().orElse(0);
    }

    @Override
    public int height(WizardContext context) {
        return Math.max(1, lines.size());
    }

    @Override
    public void render(TextGraphics tg, int column, int row, WizardContext context, ContentArea contentArea, InputArea inputArea) throws IOException {
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

    private static String normalize(String value, String name) {
        String normalized = Objects.requireNonNull(value).trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }

    static List<String> formatBulletLines(List<String> lines) {
        return Objects.requireNonNull(lines).stream()
                .map(line -> {
                    String normalized = Objects.requireNonNull(line).trim();
                    return normalized.isEmpty() ? "-" : "- " + normalized;
                })
                .toList();
    }
}
