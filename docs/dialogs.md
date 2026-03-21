# Dialogs

This document describes the current usage of:

- `TextLineDialog`
- `SingleChoiceDialog`

Both dialogs now use composition. You build the required UI areas first and then pass them into the dialog builder.

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
).build();

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

### Adding menu items

Menu items are added through `List<DialogOption>`.

The simplest implementation is `SimpleDialogOption`:

```java
List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana"),
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
                        .withArrowsNavigation()
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
).build();

Optional<DialogOption> result = dialog.show();
```

## Notes

- Both dialogs read from `/dev/tty` and write to `/dev/tty` by default.
- Both builders currently expose only construction-time composition. The dialog behavior is not configured through `DialogTheme` directly anymore.
- UI styling is applied by configuring the areas before they are passed to the dialog.
