# UI Areas

This document describes the UI building blocks from `org.dawciobiel.shelldialog.cli.ui`.

## Overview

The main UI areas are:

- `TitleArea`
- `ContentArea`
- `InputArea`
- `NavigationArea`

Dialogs are now built by composing these areas first.

## TitleArea

`TitleArea` renders one or more title lines.

### Builder methods

- `withTitle(String... lines)`
- `withTitle(List<String> lines)`
- `withTheme(DialogTheme theme)`
- `withBorderColor(TextColor color)`
- `withTitleColor(TextColor color)`

### Notes

- If a title string contains `\n`, it is split into multiple lines.
- `getHeight()` returns the number of rendered title lines.

### Example

```java
TitleArea titleArea = new TitleArea.Builder()
        .withTitle("Main title", "Second line")
        .withTheme(theme)
        .build();
```

## ContentArea

`ContentArea` renders a single line of static content.

It is used for general informational text and also as a styling template for menu items in `SingleChoiceDialog`.

### Builder methods

- `withContent(String content)`
- `withTheme(DialogTheme theme)`
- `withForegroundColor(TextColor color)`
- `withBackgroundColor(TextColor color)`

### Additional methods

- `getHeight()` returns `1`
- `withContent(String content)` creates a copy with the same colors and new text

### Example

```java
ContentArea contentArea = new ContentArea.Builder()
        .withContent("Your answer will be used in the greeting.")
        .withTheme(theme)
        .build();
```

## InputArea

`InputArea` renders a single-line input field.

In `TextLineDialog`, the configured `InputArea` acts as a style template. The dialog creates copies with the current text while the user types.

### Builder methods

- `withContent(String content)`
- `withTheme(DialogTheme theme)`
- `withForegroundColor(TextColor color)`
- `withBackgroundColor(TextColor color)`

### Additional methods

- `withContent(String content)` creates a copy with the same colors and new text

### Example

```java
InputArea inputArea = new InputArea.Builder()
        .withTheme(theme)
        .build();
```

## NavigationArea

`NavigationArea` renders the bottom toolbar with available keyboard actions.

### Builder methods

- `withToolbar(NavigationToolbar toolbar)`
- `withTheme(DialogTheme theme)`
- `withRenderer(NavigationToolbarRenderer renderer)`

### Notes

- `withTheme(theme)` creates a `NavigationToolbarRenderer` from `navigationStyle()`
- `withRenderer(...)` gives full control over toolbar colors

### Example using DialogTheme

```java
NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withTheme(theme)
        .build();
```

### Example using explicit renderer

```java
NavigationArea navigationArea = new NavigationArea.Builder()
        .withToolbar(
                NavigationToolbar.builder()
                        .withArrowsNavigation()
                        .withEnterAccept()
                        .withEscapeCancel()
                        .build()
        )
        .withRenderer(
                new NavigationToolbarRenderer(
                        TextColor.ANSI.MAGENTA_BRIGHT,
                        TextColor.ANSI.WHITE,
                        TextColor.ANSI.DEFAULT
                )
        )
        .build();
```
