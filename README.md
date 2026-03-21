# ShellDialog

**Current version: 2.1.1-SNAPSHOT**

ShellDialog is a simple Java library for creating interactive command-line menus and dialogs. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces using the Lanterna library.

## Features

- **Multiple Dialog Types:**
    - `SingleChoiceDialog`: Classic single-choice menu.
    - `TextLineDialog`: Prompt for single-line text input.
    - `PasswordDialog`: Prompt for masked password input.
- **Theming System:** Customize colors and styles using `DialogTheme`.
- **Composable UI Areas:** Build dialogs from `TitleArea`, `ContentArea`, `InputArea`, and `NavigationArea`.
- **Builder Pattern:** Fluent API for constructing dialogs and UI components.
- **Navigation Toolbar:** Customizable bottom toolbar with shortcuts.
- **Clean UI:**
    - Optional shared border around the whole dialog.
    - Automatic text wrapping for long titles.
    - Keyboard navigation support (Arrow keys, Enter, Escape, Space for multi-selection).
- **Modern Java:** Built with Java 21 features.

### Usage examples

Usage examples are available in package `org.dawciobiel.shelldialog.examples`.

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
java -jar build/libs/shelldialog-2.1.0.jar singlechoice

# Launch Text Line Dialog example
java -jar build/libs/shelldialog-2.1.0.jar textline

# Launch Password Dialog example
java -jar build/libs/shelldialog-2.1.0.jar password
```

## Version information

The library version can be checked in three ways:

- from the JAR file name, for example `shelldialog-2.1.1-SNAPSHOT.jar`
- from `META-INF/MANIFEST.MF` inside the JAR
- from the command line with `java -jar build/libs/shelldialog-2.1.1-SNAPSHOT.jar --version`
