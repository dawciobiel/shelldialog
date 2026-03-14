package org.dawciobiel.shelldialog;

import org.dawciobiel.shelldialog.console.header.border.BorderType;
import org.dawciobiel.shelldialog.menu.Menu;


public class UsageExample {

    public static void main(String[] args) {
        String[] menuItems = {"„Therefore if your right eye causes you to sin, (...) pluck it out and throw it away \"",

                " Menuitem #1 ", " Menuitem #2 ", " Menuitem #3 ", " Menuitem #4 "};

        Integer choice = Menu.create(menuItems, BorderType.BORDER_ALL);

        if (choice < 0) {
            System.err.println("Do not launch the application under IDE, Maven or Gradle. They pipe the terminal output stream.");
            System.err.println("Use a real terminal instead:");
            System.err.println("\tjava -jar build/libs/shelldialog-1.0.0-SNAPSHOT.jar");
            System.err.println("or");
            System.err.println("\tmvn clean compile exec:java");

        } else {
            System.out.println("Selected menuitem: [" + menuItems[choice] + "]");
            System.out.println("Selected menuitem index: [" + choice + "]");
        }
    }

}