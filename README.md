# ShellDialog

**Current version: 3.2.2**

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
    - `WizardDialog`: Compose multiple steps with back/next/finish navigation.
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
- `WizardDialog` supports multi-step flows with shared context, validation, and typed final result mapping.
- `WizardDialog` ships with built-in text, password, directory, file, info, and summary steps in `v1`.
- `WizardDialog` steps can expose optional per-step help text below the header.
- `WizardDialog` renders an automatic step progress bar below the header.
- `WizardInfoStep` supports bullet-formatted instructional content for onboarding-style screens.
- `WizardSummaryStep` supports typed `label: value` review rows with placeholder handling for missing values.
- `WizardSummaryStep` also supports grouped review sections for larger wizard summaries.
- `WizardSummaryStep` can add an optional intro line above grouped summary sections.
    - `MultiChoiceDialog` supports default selected options.
    - `YesNoDialog` supports a configurable default focused answer.
    - `FileDialog` supports directory navigation, filtering (by predicate or extension), smart sorting (folders first), and quick navigation shortcuts (F5, Home, End).
    - `FileDialog` can cycle selectable extension presets at runtime.
    - `FileDialog` can optionally show hidden files and toggle them at runtime with `F2`.
    - `FileDialog` can optionally show a metadata preview panel for the currently focused entry, including human-readable file sizes, last modified timestamps, and compact permissions.
    - `FileDialog` supports named extension presets for common source, text, config, and documentation file sets.
    - `FileDialog` shows the active selectable preset together with its runtime position.
    - `FileDialog` shows the preset-cycle hotkey directly in the filter line when runtime cycling is enabled.
    - `FileDialog` shows the active extension-based filter in the dialog when one is configured.
    - `FileDialog` in `directoriesOnly` mode can select the currently opened directory directly.
    - `FileDialog` shows an inline error message when a directory cannot be read and can recover via refresh or directory change.
    - `FileDialog` can create a new directory inline with `F7`.
    - `ProgressDialog` and `SpinnerDialog` support status messages and user cancellation via `Escape`.
- **Modern Java:** Built with Java 21 features (Sequenced Collections, Math.clamp, etc.).

## Documentation

Detailed documentation for specific features is available in the `docs/` directory:

- [Dialogs Overview](docs/dialogs.md) - usage guide for all dialog types.
- [WizardDialog Adapter Decision](docs/wizard-dialog-adapters.md) - architectural guidance for future wizard integrations.
- [Properties Configuration Decision](docs/properties-configuration.md) - guidance on `.properties` support versus Java builders.
- [Navigation Toolbar](docs/navigation-toolbar.md) - customization of the bottom toolbar.
- [Dialog Theme](docs/dialog-theme.md) - theming system details.
- [Dialog Options](docs/dialog-options.md) - working with `DialogOption`.

### Usage examples

Usage examples are available in package `io.github.dawciobiel.shelldialog.examples`.

The example package is covered by smoke tests to catch configuration regressions in the bundled entrypoints.

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

- `io.github.dawciobiel:shelldialog:3.2.2`

Gradle:

```gradle
implementation("io.github.dawciobiel:shelldialog:3.2.2")
```

Maven:

```xml
<dependency>
    <groupId>io.github.dawciobiel</groupId>
    <artifactId>shelldialog</artifactId>
    <version>3.2.2</version>
</dependency>
```

Artifact pages:

- `https://central.sonatype.com/artifact/io.github.dawciobiel/shelldialog`
- `https://repo1.maven.org/maven2/io/github/dawciobiel/shelldialog/3.2.2/`

## Launching

You can run the examples using the executable `-all.jar`. If no arguments are provided, an interactive menu will be shown.

```bash
# Launch interactive menu
java -jar build/libs/shelldialog-3.2.2-all.jar

# Launch Spinner Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar spinner

# Launch Progress Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar progress

# Launch File Selection Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar file

# Launch Selection Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar singlechoice

# Launch Multi Choice Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar multichoice

# Launch Text Line Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar textline

# Launch Password Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar password

# Launch Yes/No Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar yesno

# Launch Form Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar form

# Launch Wizard Dialog example
java -jar build/libs/shelldialog-3.2.2-all.jar wizard
```

## Version information

The library version can be checked in three ways:

- from the JAR file name, for example `shelldialog-3.2.2.jar` or `shelldialog-3.2.2-all.jar`
- from `META-INF/MANIFEST.MF` inside the JAR
- from the command line with `java -jar build/libs/shelldialog-3.2.2-all.jar --version`

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
