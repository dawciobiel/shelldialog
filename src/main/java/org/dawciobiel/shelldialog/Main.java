package org.dawciobiel.shelldialog;

import org.dawciobiel.shelldialog.examples.MultiChoiceExample;
import org.dawciobiel.shelldialog.examples.PasswordExample;
import org.dawciobiel.shelldialog.examples.SingleChoiceExample;
import org.dawciobiel.shelldialog.examples.TextLineExample;
import org.dawciobiel.shelldialog.examples.YesNoExample;

/**
 * Entry point used for launching bundled example dialogs from the command line.
 */
public class Main {

    private Main() {
    }

    /**
     * Launches one of the example applications.
     *
     * @param args the first argument selects the example name
     */
    public static void main(String[] args) {
        String arg = args.length > 0 ? args[0] : "singlechoice";

        switch (arg.toLowerCase()) {
            case "--version", "-v", "version" -> System.out.println(Version.get());
            case "multichoice" -> MultiChoiceExample.main(args);
            case "password" -> PasswordExample.main(args);
            case "singlechoice" -> SingleChoiceExample.main(args);
            case "textline" -> TextLineExample.main(args);
            case "yesno" -> YesNoExample.main(args);

            default -> {
                System.out.println("Unknown dialog example: [" + arg + "]");
                System.out.println("Possible dialog examples:");
                System.out.println("singlechoice, multichoice, textline, password, yesno, version");
            }
        }
    }
}
