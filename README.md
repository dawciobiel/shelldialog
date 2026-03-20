# ShellDialog

**Current version: 2.0.0-SNAPSHOT**

ShellDialog is a simple Java library for creating interactive command-line menus and dialogs. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces using the Lanterna library.

## Features

- **Multiple Dialog Types:**
    - `SingleChoiceDialog`: Classic single-choice menu.
    - `TextLineDialog`: Prompt for single-line text input.
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

### Usage examples

Usage examples in package `org.dawciobiel.shelldialog.examples`

## Requirements

- Java 21 or higher.
- A terminal that supports ANSI escape codes.

## Building

To build the project, use Gradle:

```bash
./gradlew clean build
```

## Launching

You can run the examples using the built JAR file. Pass the dialog type as an argument:

```bash
# Launch Selection Dialog example
java -jar build/libs/shelldialog-1.3.0-SNAPSHOT.jar singlechoice

# Launch Text Line Dialog example
java -jar build/libs/shelldialog-1.3.0-SNAPSHOT.jar textline
```
