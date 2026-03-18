package org.dawciobiel.shelldialog;

import org.dawciobiel.shelldialog.examples.SelectionMenuExample;
import org.dawciobiel.shelldialog.examples.TextLineQuestionExample;

public class Main {
    public static void main(String[] args) {
        String arg = args.length > 0 ? args[0] : "selection";

        switch (arg.toLowerCase()) {
            case "selection" -> SelectionMenuExample.main(args);
            case "textline" -> TextLineQuestionExample.main(args);

            default -> {
                System.out.println("Unknown dialog example: [" + arg + "]");
                System.out.println("Possible dialog examples:");
                System.out.println("selection" + ", " + "textline");
            }
        }
    }
}