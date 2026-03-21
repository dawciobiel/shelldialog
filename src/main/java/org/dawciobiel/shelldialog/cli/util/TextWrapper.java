package org.dawciobiel.shelldialog.cli.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for wrapping plain text into fixed-width lines.
 */
public class TextWrapper {

    private static final String SPACE = " ";

    private TextWrapper() {
    }

    /**
     * Wraps text on word boundaries so that each produced line fits within the supplied width.
     *
     * @param text the text to wrap
     * @param maxWidth the maximum line width
     * @return the wrapped lines in display order
     */
    public static List<String> wrap(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(SPACE);
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxWidth) {
                lines.add(currentLine.toString().stripTrailing());
                currentLine = new StringBuilder();
            }
            if (!currentLine.isEmpty()) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString().stripTrailing());
        }

        return lines;
    }
}
