# ShellDialog

**Current version: 1.1.0-SNAPSHOT**

ShellDialog is a simple Java library for creating interactive command-line menus and dialogs. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces.

## Features

- Easy menu creation with a simple API.
- **New:** Dialog for asking questions and capturing text input.
- Support for keyboard navigation (arrow keys, enter, escape).
- Customizable borders (full, horizontal, vertical, none).
- Automatic text wrapping for long menu titles.
- Clean and modern console output.

## Usage

Here is a basic example of how to use ShellDialog:

```java
    public static void main(String[] args) {
    // Show menu dialog
    String[] menuItems = {"Menu Title", "1.Item", "2.Item", "3.Item"};

    Menu menu = new Menu(menuItems);

    // Parse result
    switch (menu.show()) {
        case IntegerValue v -> System.out.println("Selected menu item [ " + menuItems[v.value()] + " ]");
        case ErrorValue v   -> System.err.println(Messages.getString("error.terminal.stream"));
        }
    }
}
```

## Requirements

- Java 21 or higher.
- A terminal that supports ANSI escape codes.

## Building

To build the project, use Maven or Gradle:

```bash
./gradlew build
```
or
```bash
./gradlew --quiet clean build
```
or
```bash
mvn clean package
```

## Launching

```bash
java -jar build/libs/shelldialog-1.1.0-SNAPSHOT.jar
```
