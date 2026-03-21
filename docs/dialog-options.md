# Dialog Options

This document describes the option model used by `SingleChoiceDialog`.

## DialogOption

`DialogOption` is the interface used by selection dialogs.

Each option must provide:

- `int getCode()`
- `String getLabel()`

The dialog renders `getLabel()` and returns the selected option object when the user confirms the selection.

## SimpleDialogOption

`SimpleDialogOption` is the default implementation of `DialogOption`.

### Constructor

```java
new SimpleDialogOption(int code, String label)
```

### Example

```java
SimpleDialogOption apple = new SimpleDialogOption(1, "Apple");
```

### Typical usage

```java
List<DialogOption> options = List.of(
        new SimpleDialogOption(1, "Apple"),
        new SimpleDialogOption(2, "Banana"),
        new SimpleDialogOption(3, "Cherry")
);
```

## Returned value from SingleChoiceDialog

`SingleChoiceDialog.show()` returns `Optional<DialogOption>`.

In practice, this usually means:

- `Optional.of(new SimpleDialogOption(...))` when the user confirms
- `Optional.empty()` when the user cancels

If needed, you can implement your own `DialogOption` type to attach more domain-specific data.
