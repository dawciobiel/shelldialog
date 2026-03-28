# Dialogs

This document describes the current usage of:

- `TextLineDialog`
- `SingleChoiceDialog`
- `MultiChoiceDialog`
- `FileDialog`
- `FormDialog`
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
- `Home`: jump to user home directory
- `End`: jump to initial working directory (CWD)
- `[KeyType]`: any custom shortcut key configured in builder
- `Enter`: enters directory or selects file
- `Escape`: clears search filter (if not empty) or cancels dialog

### Sorting behavior

`FileDialog` automatically sorts content:
1. Parent directory link (`..`)
2. Directories (alphabetical, prefixed with `/`)
3. Files (alphabetical)

### Optional builder settings

- `withInitialDirectory(Path)` to set the starting directory (defaults to current working directory)
- `directoriesOnly(boolean)` to enable directory selection mode (defaults to `false` for file selection)
- `withVisibleItemCount(int)` to limit how many items are shown at once
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
