package io.github.dawciobiel.shelldialog.cli.dialog;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import io.github.dawciobiel.shelldialog.cli.ui.ContentArea;
import io.github.dawciobiel.shelldialog.cli.ui.InputArea;

import java.io.IOException;
import java.util.Optional;

/**
 * Single step rendered inside a {@link WizardDialog}.
 */
public interface WizardStep {

    /**
     * Returns the step title shown in the wizard header.
     *
     * @return step title
     */
    String title();

    /**
     * Returns an optional single-line description rendered below the wizard header.
     *
     * @return optional step description
     */
    default Optional<String> description() {
        return Optional.empty();
    }

    /**
     * Returns the width required to render this step.
     *
     * @param context current wizard context
     * @return content width
     */
    int width(WizardContext context);

    /**
     * Returns the height required to render this step.
     *
     * @param context current wizard context
     * @return content height
     */
    int height(WizardContext context);

    /**
     * Renders the step content.
     *
     * @param tg graphics context
     * @param column start column
     * @param row start row
     * @param context current wizard context
     * @param contentArea content style area
     * @param inputArea input style area
     * @throws IOException when rendering fails
     */
    void render(
            TextGraphics tg,
            int column,
            int row,
            WizardContext context,
            ContentArea contentArea,
            InputArea inputArea
    ) throws IOException;

    /**
     * Handles a key event specific to this step.
     *
     * @param key key stroke
     */
    void handleInput(KeyStroke key);

    /**
     * Validates this step before navigation or finish.
     *
     * @return optional validation message
     */
    Optional<String> validate();

    /**
     * Commits the step state into the shared wizard context.
     *
     * @param context target context
     */
    void commit(WizardContext context);

    /**
     * Returns the cursor position relative to the provided content origin.
     *
     * @param column start column
     * @param row start row
     * @param context current wizard context
     * @return optional cursor position
     */
    default Optional<TerminalPosition> cursorPosition(int column, int row, WizardContext context) {
        return Optional.empty();
    }
}
