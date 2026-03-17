package org.dawciobiel.shelldialog.cli;

import java.util.ArrayList;
import java.util.List;

public class TextWrapper {

    private static final String SPACE = " ";

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