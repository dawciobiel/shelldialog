# Dialogs

This document describes the current usage of:

- `TextLineDialog`
- `SingleChoiceDialog`
- `MultiChoiceDialog`
- `FileDialog`
- `FormDialog`
- `WizardDialog`
- `ProgressDialog`
- `SpinnerDialog`
- `PasswordDialog`
- `YesNoDialog`
- `MessageDialog`

All dialogs now use composition. You build the required UI areas first and then pass them into the dialog builder.
The dialog builder also controls the shared frame shown around the whole dialog.

## TextLineDialog

`TextLineDialog` is used for single-line text input.

### Required parts

To build `TextLineDialog`, you need:

- `TitleArea`
- `ContentArea`
- `InputArea`
- `NavigationArea`

### Return type

`show()` returns `Optional<String>`.

- `Optional.of(value)` when the user confirms with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character`: appends typed character
- `Backspace`: removes the last character
- `Enter`: confirms input
- `Escape`: cancels dialog

### Optional validation

`TextLineDialog.Builder` supports:

- `withInitialValue(String)` to prefill the input when the dialog opens
- `withMaxLength(int)` to cap the accepted input length
- `withValidator(InputValidator)` to validate the value on `Enter`
- `withValidationMessageStyle(TextStyle)` to style the validation message below the input

#### Built-in Validators

You can use `InputValidator.BuiltIn` for common validation rules:

```java
.withValidator(InputValidator.BuiltIn.nonEmpty("Required field"))
.withValidator(InputValidator.BuiltIn.email("Invalid email format"))
.withValidator(InputValidator.BuiltIn.isInteger("Must be a number"))
```

Validators can be combined using `.and()`:

```java
.withValidator(InputValidator.BuiltIn.nonEmpty("Required").and(InputValidator.BuiltIn.isInteger("Number only")))
```

### Example

```java
TextLineDialog dialog = new TextLineDialog.Builder(titleArea, contentArea, inputArea, navigationArea)
        .withTheme(theme)
        .withInitialValue("Dawid")
        .withValidator(InputValidator.BuiltIn.nonEmpty("Name cannot be empty"))
        .build();

Optional<String> result = dialog.show();
```

## SingleChoiceDialog

`SingleChoiceDialog` is used for selecting exactly one item from a list of options.

### Required parts

To build `SingleChoiceDialog`, you need:

- `TitleArea`
- `ContentArea` for regular items
- `ContentArea` for the selected item
- `List<DialogOption>`
- `NavigationArea`

### Return type

`show()` returns `Optional<DialogOption>`.

- `Optional.of(option)` when the user confirms with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character`: appends to the search filter
- `Backspace`: removes last character from the search filter
- `ArrowUp`: moves selection up
- `ArrowDown`: moves selection down
- `Enter`: confirms selected option
- `Escape`: clears search filter (if not empty) or cancels dialog

### Live Filtering (Search)

`SingleChoiceDialog` supports live filtering. As the user types, the list of options is instantly updated to show only items containing the entered text (case-insensitive). When a filter is active, a search bar is displayed above the options.

### Optional builder settings

- `withVisibleItemCount(int)` to limit how many menu items are shown at once and enable simple viewport scrolling for longer lists

When the list is clipped by the viewport, the dialog renders `↑ more` and `↓ more` indicators above or below the visible window, plus a simple `x/y` position counter below the list.
Disabled options are rendered with a ` (disabled)` suffix, skipped by arrow navigation, and cannot be confirmed.

## FormDialog

`FormDialog` is used for collecting multiple values on a single screen.

### Required parts

To build `FormDialog`, you need:

- `TitleArea`
- `ContentArea`
- `ContentArea` for field labels
- `InputArea` for regular fields
- `InputArea` for the focused field
- `List<FormField>`
- `NavigationArea`

### Return type

`show()` returns `Optional<T>`, where `T` is defined by `withResultMapper(...)`.

- `Optional.of(value)` when the user confirms with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character`: appends typed character to the focused field
- `Backspace`: removes the last character from the focused field
- `ArrowUp` / `ReverseTab`: moves focus to the previous field
- `ArrowDown` / `Tab`: moves focus to the next field
- `Enter`: validates all fields and confirms the form
- `Escape`: cancels the dialog

### Supported field types

`FormField` currently supports:

- `FormField.text(name, label)`
- `FormField.password(name, label)`

Each field supports:

- `withInitialValue(...)`
- `withMaxLength(int)`
- `withValidator(...)`

Password fields also support:

- `withMaskCharacter(char)`

### Result mapping

`FormDialog.Builder<T>` requires `withResultMapper(Function<FormValues, T>)`.
`FormValues` provides typed accessors such as:

- `getString(name)`
- `getPassword(name)`
- `asMap()`

### Example

```java
record LoginData(String username, char[] password) {}

FormDialog<LoginData> dialog = new FormDialog.Builder<LoginData>(
        titleArea,
        contentArea,
        labelArea,
        inputArea,
        focusedInputArea,
        List.of(
                FormField.text("username", "Username")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Username required"))
                        .build(),
                FormField.password("password", "Password")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Password required").asPasswordValidator())
                        .build()
        ),
        navigationArea
)
        .withTheme(theme)
        .withResultMapper(values -> new LoginData(
                values.getString("username"),
                values.getPassword("password")
        ))
        .build();

Optional<LoginData> result = dialog.show();
```

## WizardDialog

`WizardDialog` is used for multi-step flows with shared state and typed final results.

### Required parts

To build `WizardDialog`, you need:

- a wizard title
- `List<WizardStep>`
- `withResultMapper(Function<WizardContext, T>)`

### Return type

`show()` returns `Optional<T>`.

- `Optional.of(value)` when the user completes the final step
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character` / `Backspace`: handled by the current step when supported
- `ArrowLeft`: moves to the previous step
- `ArrowRight`: validates and advances to the next step
- `Enter`: validates and advances, or finishes on the last step
- `Escape`: cancels the wizard

### Built-in step types in v1

- `WizardTextStep` for single-line text input
- `WizardPasswordStep` for masked single-line password input
- `WizardDirectoryStep` for directory path input with optional existence checks
- `WizardFileStep` for file path input with optional existence checks
- `WizardInfoStep` for read-only informational screens
- `WizardSummaryStep` for read-only review screens

Built-in steps can also expose an optional single-line description rendered below the wizard header.

### v1 scope notes

`WizardDialog` in v1 is intentionally a lightweight step orchestrator, not a full workflow engine.

Current limitations:

- built-in steps are limited to text input, password input, directory input, file input, and summary screens
- there is no branching/conditional navigation between steps
- there are no built-in adapters yet for `PasswordDialog`, `FormDialog`, or `FileDialog`

Recommended next extensions:

- optional step adapters built on top of existing dialog primitives

Architecture note:

- `WizardDialog` should remain a lightweight standalone step system
- future adapters, if added, should stay thin and explicitly scoped
- see `docs/wizard-dialog-adapters.md` for the decision note

### Shared context

Each step can commit values into `WizardContext`, and the final result is produced via `withResultMapper(...)`.

`WizardContext` supports:

- `put(key, value)`
- `get(key)`
- `getString(key)`
- `getPassword(key)`
- `getPath(key)`
- `asMap()`

### Example

```java
WizardDialog<SetupData> dialog = new WizardDialog.Builder<SetupData>(
        "Setup Wizard",
        List.of(
                WizardInfoStep.of(
                        "Welcome",
                        "Read this short note before you begin.",
                        List.of("This wizard collects a few setup values step by step.")
                ),
                WizardTextStep.builder("Account", "Enter username", "username")
                        .withDescription("This user name will appear in the generated configuration.")
                        .withValidator(InputValidator.BuiltIn.nonEmpty("Username required"))
                        .build(),
                WizardDirectoryStep.builder("Location", "Enter target directory", "targetDirectory")
                        .withDescription("Choose where generated files should be written.")
                        .withInitialValue(Path.of("./output"))
                        .build(),
                WizardFileStep.builder("Config", "Enter config file", "configFile")
                        .withDescription("Choose the output file used to persist the wizard result.")
                        .withInitialValue(Path.of("./output/config.properties"))
                        .build(),
                WizardSummaryStep.of("Summary", "Review all values before finishing.", context -> List.of(
                        "User: " + context.getString("username"),
                        "Target: " + context.getPath("targetDirectory"),
                        "Config: " + context.getPath("configFile")
                ))
        )
)
        .withTheme(theme)
        .withResultMapper(context -> new SetupData(
                context.getString("username"),
                context.getPath("targetDirectory"),
                context.getPath("configFile")
        ))
        .build();

Optional<SetupData> result = dialog.show();
```

## PasswordDialog

`PasswordDialog` is used for masked single-line password input.

### Required parts

To build `PasswordDialog`, you need:

- `TitleArea`
- `ContentArea`
- `InputArea`
- `NavigationArea`

### Return type

`show()` returns `Optional<char[]>`.

### Security note

`PasswordDialog` exposes password values as `char[]` so callers can clear them after use.
`InputValidator` can be used via `.asPasswordValidator()` adapter.

### Keyboard behavior

- `Character`: appends typed character to the password buffer
- `Backspace`: removes the last character
- `Enter`: confirms input
- `Escape`: cancels dialog

### Optional validation

`PasswordDialog.Builder` supports:

- `withInitialValue(char[])` to prefill the input when the dialog opens
- `withMaxLength(int)` to cap the accepted input length
- `withValidator(PasswordValidator)` to validate the value on `Enter`

#### Using Built-in Validators for Passwords

```java
.withValidator(InputValidator.BuiltIn.nonEmpty("Required").asPasswordValidator())
```

### Example

```java
PasswordDialog dialog = new PasswordDialog.Builder(titleArea, contentArea, inputArea, navigationArea)
        .withTheme(theme)
        .withMaxLength(16)
        .withValidator(InputValidator.BuiltIn.nonEmpty("Password required").asPasswordValidator())
        .withMaskCharacter('*')
        .build();

Optional<char[]> result = dialog.show();
```

## MultiChoiceDialog

`MultiChoiceDialog` is used for selecting any number of items from a list.

### Required parts

To build `MultiChoiceDialog`, you need:

- `TitleArea`
- `ContentArea` for regular items
- `ContentArea` for focused items
- `ContentArea` for selected items
- `ContentArea` for selected and focused items
- `List<DialogOption>`
- `NavigationArea`

### Return type

`show()` returns `Optional<List<DialogOption>>`.

- `Optional.of(list)` when the user confirms with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character`: appends to the search filter
- `Backspace`: removes last character from the search filter
- `ArrowUp`: moves focus up
- `ArrowDown`: moves focus down
- `Space`: toggles selection of the focused option
- `Enter`: confirms current selection set
- `Escape`: clears search filter (if not empty) or cancels dialog

## FileDialog

`FileDialog` is used for browsing files and directories.

### Required parts

To build `FileDialog`, you need:

- `TitleArea`
- `ContentArea` for regular items
- `ContentArea` for the selected item
- `NavigationArea`

### Return type

`show()` returns `Optional<Path>`.

- `Optional.of(path)` when the user selects a file or directory (depending on mode) with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Keyboard behavior

- `Character`: appends to the search filter (searches within current directory)
- `Backspace`: removes last character from the search filter
- `ArrowUp`: moves selection up
- `ArrowDown`: moves selection down
- `F5`: refresh current directory content
- `F2`: toggle hidden files visibility
- `F7`: create a new directory in the currently opened path
- `Home`: jump to user home directory
- `End`: jump to initial working directory (CWD)
- `[KeyType]`: any custom shortcut key configured in builder
- `Enter`: enters directory or selects file
- In `directoriesOnly(true)` mode, the dialog also shows a `./ (current directory)` option that returns the currently opened directory.
- `Escape`: clears search filter (if not empty) or cancels dialog

If the current directory cannot be read, `FileDialog` displays an inline error message and keeps the dialog open so the user can retry (`F5`) or switch to another directory via navigation or shortcuts.

When `F7` is pressed, `FileDialog` opens an inline prompt for a new directory name. Confirm with `Enter` to create the folder in the currently opened path, or cancel with `Escape`.

### Sorting behavior

`FileDialog` automatically sorts content:
1. Parent directory link (`..`)
2. Directories (alphabetical, prefixed with `/`)
3. Files (alphabetical)

### Optional builder settings

- `withInitialDirectory(Path)` to set the starting directory (defaults to current working directory)
- `directoriesOnly(boolean)` to enable directory selection mode (defaults to `false` for file selection)
- `withVisibleItemCount(int)` to limit how many items are shown at once
- `withShowHiddenFiles(boolean)` to control whether hidden files/directories are visible
- `withFileFilter(Predicate<Path>)` to provide a custom filter for files (directories are always shown)
- `withExtensions(List<String>)` to show only files with specific extensions (e.g., `java`, `md`)
- `withShortcuts(Map<KeyType, Path>)` to configure custom keyboard shortcuts to specific directories

### Example

```java
Map<KeyType, Path> shortcuts = Map.of(
    KeyType.F1, Paths.get(System.getProperty("user.home"), "Desktop"),
    KeyType.F2, Paths.get(System.getProperty("user.home"), "Documents")
);

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withVerticalArrowsNavigation()
                        .withKey(KeyType.F1, "Desktop")
                        .withKey(KeyType.F2, "Docs")
                        .withF5Refresh()
                        .withHomeHomeDir()
                        .withEndCWD()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

FileDialog dialog = new FileDialog.Builder(titleArea, menuItemArea, selectedMenuItemArea, navigationArea)
        .withTheme(theme)
        .withShortcuts(shortcuts)
        .build();

Optional<Path> result = dialog.show();
```

## ProgressDialog

`ProgressDialog` is used for displaying a progress bar during a long-running background task.

### Required parts

To build `ProgressDialog`, you need:

- `TitleArea`
- `ContentArea` for status messages
- `ProgressTask` (the logic to execute)

### Return type

`show()` returns `Optional<Boolean>`.

- `Optional.of(true)` when the task completes successfully
- `Optional.of(false)` when the task is cancelled (via `Escape`) or fails

## SpinnerDialog

`SpinnerDialog` is used for tasks with indeterminate duration where a simple progress bar cannot be shown.

### Required parts

To build `SpinnerDialog`, you need:

- `TitleArea`
- `ContentArea` for status messages
- `ProgressTask` (the logic to execute)

### Return type

`show()` returns `Optional<Boolean>`.

- `Optional.of(true)` when the task completes successfully
- `Optional.of(false)` when the task is cancelled (via `Escape`) or fails

## YesNoDialog

`YesNoDialog` is used for binary confirmation flows.

### Required parts

To build `YesNoDialog`, you need:

- `TitleArea`
- `ContentArea`
- `ContentArea` for regular answers
- `ContentArea` for the selected answer
- `NavigationArea`

### Return type

`show()` returns `Optional<Boolean>`.

- `Optional.of(true)` when the user confirms the affirmative answer
- `Optional.of(false)` when the user confirms the negative answer
- `Optional.empty()` when the user cancels with `Escape`

## MessageDialog

`MessageDialog` is used for displaying a simple informative message with an OK button.

### Required parts

To build `MessageDialog`, you need:

- `TitleArea`
- `ContentArea`
- `NavigationArea`

### Return type

`show()` returns `Optional<Boolean>`.

- `Optional.of(true)` when the user confirms with `Enter` (OK)
- `Optional.empty()` when the user cancels with `Escape`
