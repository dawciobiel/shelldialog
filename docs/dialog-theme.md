# DialogTheme

`DialogTheme` is a style container used by UI area builders and dialog builders.

It does not render anything on its own. Instead, UI components read style values from it during construction.

## Available styles

`DialogTheme` currently exposes:

- `borderStyle()`
- `titleStyle()`
- `contentStyle()`
- `inputStyle()`
- `navigationStyle()`
- `menuItemStyle()`
- `menuItemSelectedStyle()`

Each style is a `TextStyle` with foreground and background colors.

## Builder API

You create a theme with `DialogTheme.builder()`:

```java
DialogTheme theme = DialogTheme.builder()
        .borderStyle(TextStyle.of(TextColor.ANSI.BLUE, TextColor.ANSI.DEFAULT))
        .titleStyle(TextStyle.of(TextColor.ANSI.WHITE, TextColor.ANSI.DEFAULT))
        .contentStyle(TextStyle.of(TextColor.ANSI.CYAN, TextColor.ANSI.DEFAULT))
        .inputStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
        .navigationStyle(TextStyle.of(TextColor.ANSI.BLACK_BRIGHT, TextColor.ANSI.DEFAULT))
        .menuItemStyle(TextStyle.of(TextColor.ANSI.DEFAULT, TextColor.ANSI.DEFAULT))
        .menuItemSelectedStyle(TextStyle.of(TextColor.ANSI.BLACK, TextColor.ANSI.WHITE))
        .build();
```

You can also use the predefined theme:

```java
DialogTheme theme = DialogTheme.darkTheme();
```

## Which UI area uses which style

- `TitleArea.withTheme(theme)` uses `titleStyle()`
- `ContentArea.withTheme(theme)` uses `contentStyle()`
- `InputArea.withTheme(theme)` uses `inputStyle()`
- `NavigationArea.withTheme(theme)` uses `navigationStyle()`
- `TextLineDialog.Builder.withTheme(theme)` uses `borderStyle()`
- `SingleChoiceDialog.Builder.withTheme(theme)` uses `borderStyle()`
- `PasswordDialog.Builder.withTheme(theme)` uses `borderStyle()`
- `MultiChoiceDialog.Builder.withTheme(theme)` uses `borderStyle()`
- `YesNoDialog.Builder.withTheme(theme)` uses `borderStyle()`

The menu-specific styles are intended for choice dialogs:

- `menuItemStyle()` for non-selected items
- `menuItemSelectedStyle()` for the selected item

## Important detail

`DialogTheme` is applied when the UI area is built.

That means this:

```java
ContentArea area = new ContentArea.Builder()
        .withTheme(theme)
        .build();
```

copies the colors from `theme` into the area. Changing the theme object later does not update already built UI areas.

The same applies to dialog builders:

```java
TextLineDialog dialog = new TextLineDialog.Builder(titleArea, contentArea, inputArea, navigationArea)
        .withTheme(theme)
        .build();
```

In this case `borderStyle()` is copied into the dialog configuration during construction.
