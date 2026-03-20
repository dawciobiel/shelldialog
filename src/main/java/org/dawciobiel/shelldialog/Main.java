package org.dawciobiel.shelldialog;

import org.dawciobiel.shelldialog.examples.SingleChoiceExample;
import org.dawciobiel.shelldialog.examples.TextLineExample;

public class Main {
    public static void main(String[] args) {
        String arg = args.length > 0 ? args[0] : "singlechoice";

        switch (arg.toLowerCase()) {
            case "singlechoice" -> SingleChoiceExample.main(args);
            case "textline" -> TextLineExample.main(args);

            default -> {
                System.out.println("Unknown dialog example: [" + arg + "]");
                System.out.println("Possible dialog examples:");
                System.out.println("singlechoice" + ", " + "textline");
            }
        }
    }
}