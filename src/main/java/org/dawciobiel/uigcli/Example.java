package org.dawciobiel.uigcli;

import org.dawciobiel.uigcli.console.header.border.BorderType;
import org.dawciobiel.uigcli.menu.Menu;


public class Example {

    static void main(String[] args) {
        String[] menuItems = {
                "„Jeśli więc prawe twoje oko jest ci powodem do grzechu, "
                        + " wyłup je i odrzuć od siebie................. ",
                /*
                    + "Lepiej bowiem jest dla ciebie, gdy zginie"
                    + " jeden z twoich członków, niż żeby całe twoje ciało miało być wrzucone do piekła. "
                    + "I jeśli prawa twoja ręka jest ci powodem do grzechu, odetnij ją i odrzuć od siebie. "
                    + "Lepiej bowiem jest dla ciebie, gdy zginie jeden z twoich członków, " +
                    "niż żeby całe twoje ciało miało iść do piekła”.",
                */

                " Menuitem #1 ",
                " Menuitem #2 ",
                " Menuitem #3 ",
                " Menuitem #4 "
        };

        Integer choice = Menu.create(menuItems, BorderType.BORDER_ALL);

    }

}