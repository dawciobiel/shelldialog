# Navigation Toolbar

The navigation toolbar is a UI component displayed at the bottom of a dialog, showing available keyboard shortcuts and actions.

It consists of two main parts:
- `NavigationToolbar`: Defines the content (items, labels, separators).
- `NavigationToolbarRenderer`: Handles the drawing logic and coloring.

Both are usually wrapped in a `NavigationArea`, which integrates them into the dialog layout.

## Building a Toolbar

Use `NavigationToolbar.Builder` to construct the toolbar content.

### Standard Items

The builder provides methods for common navigation actions, which use localized labels from the library resources:

- `withArrowsNavigation()` / `withVerticalArrowsNavigation()`: Adds vertical arrow keys (e.g., `↑↓ Navigate`).
- `withHorizontalArrowsNavigation()`: Adds horizontal arrow keys (e.g., `← → Navigate`).
- `withSpaceSelect()`: Adds Space key for selection (e.g., `Space Select`).
- `withEnterAccept()`: Adds Enter key for confirmation (e.g., `↵ Accept`).
- `withEscapeCancel()`: Adds Escape key for cancellation (e.g., `Esc Cancel`).

### Customizing Separators

You can customize the characters used to separate items and parts of an item:

- `itemSeparator(String)`: Separates distinct actions (default is ` | `).
- `hotkeyLabelSeparator(String)`: Separates the key from the label (default is ` `).

### Example

```java
NavigationToolbar toolbar = NavigationToolbar.builder()
        .withArrowsNavigation()
        .withEnterAccept()
        .withEscapeCancel()
        .itemSeparator("  •  ")
        .build();
```

## Rendering

The appearance of the toolbar is controlled by `NavigationToolbarRenderer` or implicitly via `DialogTheme`.

### Using DialogTheme (Recommended)

The simplest way to style the toolbar is to pass a `DialogTheme` to `NavigationArea`. The area will automatically create a renderer using the theme's navigation colors.

```java
DialogTheme theme = DialogTheme.darkTheme();

NavigationArea area = new NavigationArea.Builder()
        .withToolbar(toolbar)
        .withTheme(theme)
        .build();
```

### Using Custom Colors

If you need specific colors for the toolbar that differ from the theme, you can provide a custom `NavigationToolbarRenderer`.

The renderer takes three colors:
1.  **Hotkey Color**: Color of the key (e.g., `Esc`, `Enter`).
2.  **Label Color**: Color of the action description (e.g., `Cancel`, `Accept`).
3.  **Background Color**: Background color for the entire toolbar strip.

```java
NavigationToolbarRenderer renderer = new NavigationToolbarRenderer(
        TextColor.ANSI.YELLOW_BRIGHT, // Hotkeys
        TextColor.ANSI.WHITE,         // Labels
        TextColor.ANSI.BLUE           // Background
);

NavigationArea area = new NavigationArea.Builder()
        .withToolbar(toolbar)
        .withRenderer(renderer)
        .build();
```

## Integration

Once built, the `NavigationArea` is passed to the dialog builder along with other areas.

```java
SingleChoiceDialog dialog = new SingleChoiceDialog.Builder(
        titleArea,
        contentArea,
        selectedContentArea,
        options,
        navigationArea // <---
)
        .build();
```
