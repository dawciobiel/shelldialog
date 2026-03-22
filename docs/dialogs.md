# Dialogs

This document describes the current usage of:

- `TextLineDialog`
- `SingleChoiceDialog`
- `MultiChoiceDialog`
- `PasswordDialog`
- `YesNoDialog`

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

`TextLineDialog.Builder` also supports:

- `withInitialValue(String)` to prefill the input when the dialog opens
- `withMaxLength(int)` to cap the accepted input length
- `withValidator(Function<String, Optional<String>>)` to validate the value on `Enter`
- `withValidationMessageStyle(TextStyle)` to style the validation message below the input

If the validator returns an error message, the dialog stays open and renders that message below the input field.

### Example

```java
DialogTheme theme = DialogTheme.darkTheme();

TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Please enter your name:")
        .withTheme(theme)
        .build();

ContentArea contentArea = new ContentArea.Builder()
        .withContent("Your answer will be used in the greeting.")
        .withTheme(theme)
        .build();

InputArea inputArea = new InputArea.Builder()
        .withTheme(theme)
        .build();

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

TextLineDialog dialog = new TextLineDialog.Builder(
        titleArea,
        contentArea,
        inputArea,
        navigationArea
)
        .withBorder(true)
        .withTheme(theme)
        .withInitialValue("Dawid")
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

- `ArrowUp`: moves selection up
- `ArrowDown`: moves selection down
- `Enter`: confirms selected option
- `Escape`: cancels dialog

### Optional builder settings

- `withVisibleItemCount(int)` to limit how many menu items are shown at once and enable simple viewport scrolling for longer lists

When the list is clipped by the viewport, the dialog renders `↑ more` and `↓ more` indicators above or below the visible window, plus a simple `x/y` position counter below the list.
Disabled options are rendered with a ` (disabled)` suffix, skipped by arrow navigation, and cannot be confirmed.

### Adding menu items

Menu items are added through `List<DialogOption>`.

The simplest implementation is `SimpleDialogOption`:

```java
List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana", false),
        new SimpleDialogOption(3, "Cherry")
);
```

### Example

```java
DialogTheme theme = DialogTheme.darkTheme();

TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Select your favorite fruit:")
        .withTheme(theme)
        .build();

ContentArea menuItemArea = new ContentArea.Builder()
        .withTheme(theme)
        .build();

ContentArea selectedMenuItemArea = new ContentArea.Builder()
        .withForegroundColor(theme.menuItemSelectedStyle().foreground())
        .withBackgroundColor(theme.menuItemSelectedStyle().background())
        .build();

List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana")
);

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withVerticalArrowsNavigation()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(
        titleArea,
        menuItemArea,
        selectedMenuItemArea,
        options,
        navigationArea
)
        .withBorder(true)
        .withTheme(theme)
        .withVisibleItemCount(3)
        .build();

Optional<DialogOption> result = dialog.show();
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

- `Optional.of(value)` when the user confirms with `Enter`
- `Optional.empty()` when the user cancels with `Escape`

### Security note

`PasswordDialog` exposes password values as `char[]` so callers can clear them after use.
This reduces the lifetime of sensitive data compared to returning immutable `String` values, but it does not protect against memory inspection, profilers, debuggers, or other tools with sufficient access to the running JVM process.

### Keyboard behavior

- `Character`: appends typed character to the password buffer
- `Backspace`: removes the last character
- `Enter`: confirms input
- `Escape`: cancels dialog

### Optional validation

`PasswordDialog.Builder` also supports:

- `withInitialValue(char[])` to prefill the input when the dialog opens
- `withMaxLength(int)` to cap the accepted input length
- `withValidator(Function<char[], Optional<String>>)` to validate the value on `Enter`
- `withValidationMessageStyle(TextStyle)` to style the validation message below the input

If the validator returns an error message, the dialog stays open and renders that message below the input field.

### Example

```java
DialogTheme theme = DialogTheme.darkTheme();

TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Enter your password")
        .withTheme(theme)
        .build();

ContentArea contentArea = new ContentArea.Builder()
        .withContent("The typed password is masked on screen.")
        .withTheme(theme)
        .build();

InputArea inputArea = new InputArea.Builder()
        .withTheme(theme)
        .build();

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

PasswordDialog dialog = new PasswordDialog.Builder(
        titleArea,
        contentArea,
        inputArea,
        navigationArea
)
        .withTheme(theme)
        .withInitialValue("secret".toCharArray())
        .withMaxLength(16)
        .withValidator(value -> value.length < 6
                ? Optional.of("Password must be at least 6 characters long.")
                : Optional.empty())
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

- `ArrowUp`: moves focus up
- `ArrowDown`: moves focus down
- `Space`: toggles selection of the focused option
- `Enter`: confirms current selection set
- `Escape`: cancels dialog

### Optional builder settings

- `withInitiallySelectedOptions(List<DialogOption>)` to preselect options when the dialog opens
- `withVisibleItemCount(int)` to limit how many menu items are shown at once and enable simple viewport scrolling for longer lists

When the list is clipped by the viewport, the dialog renders `↑ more` and `↓ more` indicators above or below the visible window, plus a simple `x/y` position counter below the list.
Disabled options are rendered with a ` (disabled)` suffix, skipped by arrow navigation, and cannot be toggled with `Space`.

### Example

```java
DialogTheme theme = DialogTheme.darkTheme();

TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Select your favorite fruits:")
        .withTheme(theme)
        .build();

ContentArea menuItemArea = new ContentArea.Builder()
        .withTheme(theme)
        .build();

ContentArea focusedMenuItemArea = new ContentArea.Builder()
        .withForegroundColor(TextColor.ANSI.BLACK)
        .withBackgroundColor(TextColor.ANSI.YELLOW_BRIGHT)
        .build();

ContentArea selectedMenuItemArea = new ContentArea.Builder()
        .withForegroundColor(TextColor.ANSI.BLACK)
        .withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT)
        .build();

ContentArea selectedFocusedMenuItemArea = new ContentArea.Builder()
        .withForegroundColor(TextColor.ANSI.BLACK)
        .withBackgroundColor(TextColor.ANSI.WHITE)
        .build();

List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana")
);

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withVerticalArrowsNavigation()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

MultiChoiceDialog dialog = new MultiChoiceDialog.Builder(
        titleArea,
        menuItemArea,
        focusedMenuItemArea,
        selectedMenuItemArea,
        selectedFocusedMenuItemArea,
        options,
        navigationArea
)
        .withTheme(theme)
        .withInitiallySelectedOptions(List.of(options.get(1)))
        .withVisibleItemCount(3)
        .build();

Optional<List<DialogOption>> result = dialog.show();
```

## Notes

- All dialogs read from `/dev/tty` and write to `/dev/tty` by default.
- UI areas control only their own content styles.
- The shared dialog frame is configured on the dialog builder through `withBorder(...)`, `withBorderColor(...)`, `withBorderStyle(...)`, or `withTheme(...)`.

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

### Keyboard behavior

- `ArrowLeft`: selects the affirmative answer
- `ArrowRight`: selects the negative answer
- `Enter`: confirms the selected answer
- `Escape`: cancels dialog

### Optional builder settings

- `withDefaultYesSelected(boolean)` to choose which answer is focused when the dialog opens

### Example

```java
DialogTheme theme = DialogTheme.darkTheme();

TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Confirm action")
        .withTheme(theme)
        .build();

ContentArea contentArea = new ContentArea.Builder()
        .withContent("Do you want to continue?")
        .withTheme(theme)
        .build();

ContentArea answerArea = new ContentArea.Builder()
        .withTheme(theme)
        .build();

ContentArea selectedAnswerArea = new ContentArea.Builder()
        .withForegroundColor(TextColor.ANSI.BLACK)
        .withBackgroundColor(TextColor.ANSI.GREEN_BRIGHT)
        .build();

NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withHorizontalArrowsNavigation()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();

YesNoDialog dialog = new YesNoDialog.Builder(
        titleArea,
        contentArea,
        answerArea,
        selectedAnswerArea,
        navigationArea
)
        .withTheme(theme)
        .withDefaultYesSelected(false)
        .build();

Optional<Boolean> result = dialog.show();
```
