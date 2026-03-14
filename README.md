# ShellDialog

ShellDialog is a simple Java library for creating interactive command-line menus. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces.

## Features

- Easy menu creation with a simple API.
- Support for keyboard navigation (arrow keys, enter, escape).
- Customizable borders (full, horizontal, vertical, none).
- Automatic text wrapping for long menu titles.
- Clean and modern console output.

## Usage

Here is a basic example of how to use ShellDialog:

```java
import org.dawciobiel.shelldialog.menu.Menu;
import org.dawciobiel.shelldialog.console.header.border.BorderType;

public class Example {
    static void main(String[] args) {
        String[] menuItems = {
            "Main Menu Title",
            "Option 1",
            "Option 2",
            "Option 3",
            "Exit"
        };

        // Create and show the menu
        Integer choice = Menu.create(menuItems, BorderType.BORDER_ALL);

        if (choice < 0) {
            System.out.println("Menu cancelled (Escape pressed).");
        } else {
            System.out.println("Selected option index: " + choice);
            System.out.println("Selected option: " + menuItems[choice]);
        }
    }
}
```

## Requirements

- Java 17 or higher.
- A terminal that supports ANSI escape codes.

## Building

To build the project, use Maven or Gradle:

```bash
mvn clean package
```

or

```bash
./gradlew build
```
