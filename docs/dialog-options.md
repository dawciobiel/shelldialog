# Dialog Options

This document describes the option model used by `SingleChoiceDialog`.

## DialogOption

`DialogOption` is the interface used by selection dialogs.

Each option must provide:

- `int getCode()`
- `String getLabel()`
- `boolean isEnabled()`

The dialog renders `getLabel()` and returns the selected option object when the user confirms the selection.

## SimpleDialogOption

`SimpleDialogOption` is the default implementation of `DialogOption`.

### Constructors

```java
new SimpleDialogOption(int code, String label)
new SimpleDialogOption(int code, String label, boolean enabled)
```

### Example

```java
SimpleDialogOption apple = new SimpleDialogOption(1, "Apple");
SimpleDialogOption banana = new SimpleDialogOption(2, "Banana", false);
```

### Typical usage

```java
List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana", false),
        new SimpleDialogOption(3, "Cherry")
);
```

Disabled options are rendered as unavailable, skipped during keyboard navigation, and cannot be confirmed or toggled.

## Returned value from SingleChoiceDialog

`SingleChoiceDialog.show()` returns `Optional<DialogOption>`.

In practice, this usually means:

- `Optional.of(new SimpleDialogOption(...))` when the user confirms
- `Optional.empty()` when the user cancels

If needed, you can implement your own `DialogOption` type to attach more domain-specific data.
