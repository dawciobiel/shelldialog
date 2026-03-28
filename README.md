# ShellDialog

**Current version: 3.1.0**

ShellDialog is a simple Java library for creating interactive command-line menus and dialogs. It provides a clean and easy-to-use API for displaying menus, handling user input, and creating visually appealing text-based user interfaces using the Lanterna library.

## Visual Preview

### List Selection with Live Filtering
```text
  ┌──────────────────────────────────────────────────────────────┐
  │                 Select your favorite fruit:                  │
  ├──────────────────────────────────────────────────────────────┤
  │                                                              │
  │  Search: app_                                                │
  │                                                              │
  │  ❯ Apple                                                     │
  │    Pineapple                                                 │
  │    (no more results)                                         │
  │                                                              │
  │  1/2                                                         │
  │                                                              │
  ├──────────────────────────────────────────────────────────────┤
  │  ↑↓ Navigation  |  ↵ Accept  |  Esc Cancel                   │
  └──────────────────────────────────────────────────────────────┘
```

### Informative Message Dialog
```text
  ╔══════════════════════════════════════════════════════════════╗
  ║                      System Notification                     ║
  ╠══════════════════════════════════════════════════════════════╣
  ║                                                              ║
  ║  The background synchronization process has completed        ║
  ║  successfully. All your files are up to date.                ║
  ║                                                              ║
  ╟──────────────────────────────────────────────────────────────╢
  ║  ↵ OK  |  Esc Cancel                                         ║
  ╚══════════════════════════════════════════════════════════════╝
```

## Features

- **Multiple Dialog Types:**
    - `SingleChoiceDialog`: Classic single-choice menu.
    - `MultiChoiceDialog`: Select any number of options from a list.
    - `FileDialog`: Select a file or directory from the file system.
    - `FormDialog`: Collect multiple text/password values on a single screen.
    - `ProgressDialog`: Show a progress bar for background tasks.
    - `SpinnerDialog`: Show an animated spinner for indeterminate tasks.
    - `TextLineDialog`: Prompt for single-line text input.
    - `PasswordDialog`: Prompt for masked password input.
    - `YesNoDialog`: Confirm or decline an action.
    - `MessageDialog`: Show simple alerts and informative messages.
- **Live Filtering:** Type anywhere in list-based dialogs (`SingleChoiceDialog`, `MultiChoiceDialog`, `FileDialog`) to instantly search and filter options.
- **Theming System:** Customize colors and styles using `DialogTheme`.
- **Composable UI Areas:** Build dialogs from `TitleArea`, `ContentArea`, `InputArea`, and `NavigationArea`.
- **Builder Pattern:** Fluent API for constructing dialogs and UI components.
- **Navigation Toolbar:** Customizable bottom toolbar with shortcuts (see [documentation](docs/navigation-toolbar.md)).
- **Input Validation:** Built-in and custom validators for `TextLineDialog` and `PasswordDialog` (including email, numbers, regex, and non-empty checks).
- **Clean UI:**
    - Optional shared border around the whole dialog.
    - Automatic text wrapping for long titles.
    - Keyboard navigation support (Arrow keys, Enter, Escape, Space for multi-selection).
- **Useful Input Defaults:**
    - `TextLineDialog` supports an initial value plus max length and validation on `Enter`.
    - `PasswordDialog` supports an initial value plus max length and validation on `Enter`.
    - `FormDialog` supports typed result mapping, per-field validation, and text/password fields in one dialog.
    - `MultiChoiceDialog` supports default selected options.
    - `YesNoDialog` supports a configurable default focused answer.
    - `FileDialog` supports directory navigation, filtering (by predicate or extension), smart sorting (folders first), and quick navigation shortcuts (F5, Home, End).
    - `ProgressDialog` and `SpinnerDialog` support status messages and user cancellation via `Escape`.
- **Modern Java:** Built with Java 21 features (Sequenced Collections, Math.clamp, etc.).

## Documentation

Detailed documentation for specific features is available in the `docs/` directory:

- [Dialogs Overview](docs/dialogs.md) - usage guide for all dialog types.
- [Navigation Toolbar](docs/navigation-toolbar.md) - customization of the bottom toolbar.
- [Dialog Theme](docs/dialog-theme.md) - theming system details.
- [Dialog Options](docs/dialog-options.md) - working with `DialogOption`.

### Usage examples

Usage examples are available in package `io.github.dawciobiel.shelldialog.examples`.

## Requirements

- Java 21 or higher.
- A terminal that supports ANSI escape codes.

## Building

To build the project, use Gradle:

```bash
./gradlew clean build
```

To build the executable fat JAR used for running examples locally:

```bash
./gradlew fatJar
```

## Dependency

The library is available from Maven Central under:

- `io.github.dawciobiel:shelldialog:3.1.0`

Gradle:

```gradle
implementation("io.github.dawciobiel:shelldialog:3.1.0")
```

Maven:

```xml
<dependency>
    <groupId>io.github.dawciobiel</groupId>
    <artifactId>shelldialog</artifactId>
    <version>3.1.0</version>
</dependency>
```

Artifact pages:

- `https://central.sonatype.com/artifact/io.github.dawciobiel/shelldialog`
- `https://repo1.maven.org/maven2/io/github/dawciobiel/shelldialog/3.1.0/`

## Launching

You can run the examples using the executable `-all.jar`. If no arguments are provided, an interactive menu will be shown.

```bash
# Launch interactive menu
java -jar build/libs/shelldialog-3.1.0-all.jar

# Launch Spinner Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar spinner

# Launch Progress Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar progress

# Launch File Selection Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar file

# Launch Selection Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar singlechoice

# Launch Multi Choice Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar multichoice

# Launch Text Line Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar textline

# Launch Password Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar password

# Launch Yes/No Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar yesno

# Launch Form Dialog example
java -jar build/libs/shelldialog-3.1.0-all.jar form
```

## Version information

The library version can be checked in three ways:

- from the JAR file name, for example `shelldialog-3.1.0.jar` or `shelldialog-3.1.0-all.jar`
- from `META-INF/MANIFEST.MF` inside the JAR
- from the command line with `java -jar build/libs/shelldialog-3.1.0-all.jar --version`

## Contributing

Contributions are welcome and appreciated.

You can help improve ShellDialog by:

- Reporting bugs
- Suggesting new features or API improvements
- Improving documentation
- Submitting pull requests

### Reporting Issues

Please use the **Issues** tab and choose the appropriate template:

- Bug report
- Feature request

Before opening a new issue, check existing ones to avoid duplicates.

### Pull Requests

If you would like to contribute code:

1. Fork the repository
2. Create a feature branch from `main`
3. Keep changes focused and minimal
4. Ensure the project builds successfully
5. Add or update tests if applicable
6. Open a Pull Request using the provided template

Please see `CONTRIBUTING.md` for detailed contribution guidelines.
