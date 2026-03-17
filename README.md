# ShellDialog

**Current version: 1.2.0-SNAPSHOT**

ShellDialog is a simple Java library for creating interactive command-line menus and dialogs. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces using the Lanterna library.

## Features

- **Multiple Dialog Types:**
    - `SelectionMenu`: Classic single-choice menu.
    - `MultiChoiceDialog`: Select multiple options from a list.
    - `TextLineQuestion`: Prompt for single-line text input.
    - `PasswordDialog`: Secure text input (masked characters).
    - `YesNoDialog`: Simple binary choice (Yes/No).
- **Theming System:** Customize colors and styles using `DialogTheme`.
- **Builder Pattern:** Fluent API for constructing dialogs.
- **Navigation Toolbar:** Customizable bottom toolbar with shortcuts.
- **Clean UI:**
    - Customizable borders.
    - Automatic text wrapping for long titles.
    - Keyboard navigation support (Arrow keys, Enter, Escape, Space for multi-selection).
- **Modern Java:** Built with Java 21 features.

## Usage

Here is a basic example of how to use ShellDialog with the new Builder API:

### Selection Menu

```java
public static void main(String[] args) {
    // Define menu items (first item is title)
    String[] menuItems = {
        "Main Menu",
        "1. Option One",
        "2. Option Two",
        "3. Exit"
    };

    // Build the menu
    SelectionMenu menu = new SelectionMenu.Builder(menuItems)
        .theme(DialogTheme.darkTheme()) // Optional: set theme
        .build();

    // Show dialog and handle result
    Value result = menu.show();

    switch (result) {
        case IntegerValue v -> System.out.println("Selected index: " + v.value());
        case TextValue v -> {
            if (Showable.DIALOG_CANCELED_FLAG.equals(v.getTextValue())) {
                System.out.println("Dialog canceled");
            }
        }
        case ErrorValue v -> System.err.println("Error: " + v.message());
        default -> {}
    }
}
```

### Text Input Dialog

```java
TextLineQuestion dialog = new TextLineQuestion.Builder("What is your name?")
    .build();

Value result = dialog.show();
if (result instanceof TextValue v) {
    System.out.println("Hello, " + v.value());
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
mvn clean package
```

## Launching

```bash
java -jar build/libs/shelldialog-1.2.0-SNAPSHOT.jar
```
