# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachanglog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- `MessageDialog` for simple informative alerts and notifications.
- `FormDialog<T>` for collecting multiple text/password inputs on a single screen with typed result mapping via `withResultMapper(...)`.
- `WizardDialog<T>` for multi-step flows with shared context, built-in text/summary steps, and typed final result mapping.
- **Input Validation System:** New `InputValidator` and `PasswordValidator` interfaces with built-in rules (`nonEmpty`, `email`, `isInteger`, `regex`, `maxLength`).
- `FileDialog` enhancements:
    - **Smart Sorting:** Directories are now listed first, followed by files.
    - **Hidden Files Toggle:** Added `withShowHiddenFiles(boolean)` plus runtime toggle with `F2`.
    - **Current Directory Selection:** `directoriesOnly(true)` mode now exposes a selectable current-directory entry.
    - **Read Error Feedback:** unreadable directories now show an inline error message and recover on successful refresh/navigation.
    - **Inline Folder Creation:** Added `F7` new-folder flow directly inside `FileDialog`.
    - **Navigation Shortcuts:** Added `F5` (Refresh), `Home` (User Home), and `End` (CWD).
    - **Custom Shortcuts:** Support for user-defined keyboard shortcuts to specific directories via `.withShortcuts()`.
- **Navigation Toolbar:** Added support for custom keys (`withKey`) and specialized actions (`withEnterOK`, `withF5Refresh`, etc.).
- **Visual Preview:** ASCII Art previews in README for better documentation.
- New unit test suites for `InputValidator`, `MessageDialog`, and `NavigationToolbar`.

### Changed
- **Modern Java:** Codebase updated to leverage Java 21 features (`SequencedCollection`, `Math.clamp`).
- `FileExample` now demonstrates custom shortcuts and new navigation features.
- `Main` interactive gallery now includes `MessageDialog` and `FormDialog`.

## [3.1.0] - 2026-03-23

### Added
- `FileDialog` for selecting files and directories from the file system.
- `ProgressDialog` for displaying a progress bar during long-running background tasks.
- `SpinnerDialog` for background tasks with indeterminate duration.
- **Live Filtering:** Instant search support in all list-based dialogs (`SingleChoiceDialog`, `MultiChoiceDialog`, `FileDialog`).
- **Dependency Injection:** Support for injecting `Terminal` and `InputStream`/`OutputStream` into dialogs, facilitating stable interaction testing.
- `InteractionFlowTest` suite for simulating real keyboard interactions.
- `FileExample`, `ProgressExample`, and `SpinnerExample` added to the examples gallery.
- New interactive examples gallery in `Main` class with the library version displayed in the title.

### Changed
- `AbstractListDialog` now handles common list navigation, viewport logic, and filtering.
- `AbstractDialog` refactored to support multiple input sources (Streams, Paths, or direct Terminal).
- `MultiChoiceDialog` now uses object-based selection tracking instead of indices to maintain state during filtering.
- `SimpleDialogOption` now correctly implements `equals()` and `hashCode()`.
- Builder `withTheme()` methods now correctly propagate all relevant theme styles (e.g., validation message style).

### Fixed
- `NullPointerException` in `NavigationArea` when no theme or renderer was provided.
- `NullPointerException` in `ProgressDialog` when using `Optional.of(null)`.

## [3.0.0] - 2026-03-22

### Added
- Maven Central-ready publication setup with sources, Javadoc, signing, and Central publishing metadata.
- Dedicated `fatJar` / `-all.jar` artifact for running bundled CLI examples without changing the library artifact.

### Changed
- **Breaking Change:** Maven coordinates changed from `org.dawciobiel:shelldialog` to `io.github.dawciobiel:shelldialog`.
- **Breaking Change:** Java package namespace changed from `org.dawciobiel.shelldialog` to `io.github.dawciobiel.shelldialog`.
- Standard `jar` is now the library artifact for dependency consumption, while the executable artifact is published separately as `-all.jar`.
- Publishing, README, and user documentation were updated for the new namespace and artifact layout.

## [2.2.0] - 2026-03-22

### Added
- `YesNoDialog` for binary confirmation flows with configurable labels and horizontal navigation.
- `PasswordDialog` for masked password entry returning `char[]`.
- `MultiChoiceDialog` with separate visual states for focused, selected, and selected+focused rows.
- New examples: `YesNoExample`, `PasswordExample`, and `MultiChoiceExample`.
- Version information in JAR manifests and CLI support for `--version`, `-v`, and `version`.
- JUnit 5 test suite covering dialog layout, area measurement, version fallback, and dialog builder configuration.
- Navigation toolbar support for horizontal arrows and `Space`-to-select labels.

### Changed
- Dialog border rendering now uses a shared frame configured directly on dialog builders.
- Dialog border color and visibility are configured on dialog builders instead of UI areas.
- `NavigationToolbar` now distinguishes vertical and horizontal arrow navigation items.
- README and user documentation were updated for the current dialog API and examples.

### Removed
- Public `BorderType` API.

## [2.1.0] - 2026-03-21

### Added
- `contentStyle` in `DialogTheme` and its builder for `ContentArea` styling.
- `withContent(String)` in `ContentArea` to support styled content templates.

### Changed
- **Breaking Change:** `TextLineDialog` is now composed from preconfigured `TitleArea`, `ContentArea`, `InputArea`, and `NavigationArea`.
- **Breaking Change:** `SingleChoiceDialog` is now composed from preconfigured `TitleArea`, `NavigationArea`, and `ContentArea` templates for regular and selected menu items.
- Updated `TextLineExample` and `SingleChoiceExample` to demonstrate explicit UI area composition and per-component styling.
- Updated project version in Gradle and Maven to `2.1.0`.

## [2.0.0-SNAPSHOT] - 2024-05-23

**Major Release** introducing a completely new architecture for Dialogs and UI components. This release is not backward compatible.

### Added
- New UI components: `TitleArea`, `ContentArea`, `InputArea`, `NavigationArea` with Builder pattern.
- `DialogOption` interface and `SimpleDialogOption` implementation for typed selection results.
- New examples: `SingleChoiceExample` and `TextLineExample`.
- `cli.style` package for UI style definitions (`Arrow`, `BorderLine`).

### Changed
- **Breaking Change:** Complete architecture overhaul of Dialog classes.
- **Breaking Change:** Dialogs now return `Optional<T>` instead of custom `Value` types.
- **Breaking Change:** `SelectionDialog` renamed to `SingleChoiceDialog`.
- **Breaking Change:** `AbstractDialog` is now generic `AbstractDialog<T>`.
- `BorderLine` constants now loaded from `ui.properties`.
- Titles now support multi-line text via `List<String>` in `TitleArea`.
- Updated `pom.xml` to version `2.0.0-SNAPSHOT`.

### Removed
- **Breaking Change:** `Value`, `TextValue`, `IntegerValue`, `ErrorValue` result classes.
- Old examples: `SelectionMenuExample`, `TextLineQuestionExample`.
- Frame borders from titles (temporarily) to simplify rendering.

## [1.3.0-SNAPSHOT] - 2024-05-21

### Added
- `menuItemStyle` and `menuItemSelectedStyle` to `DialogTheme` for custom menu item styling.
- Main application entry point in `Main.java`.

### Changed
- **Breaking Change:** Renamed `SelectionMenu` to `SelectionDialog`.
- **Breaking Change:** Renamed `TextLineQuestion` to `TextLineDialog`.
- Refactored `NavigationLabels` to use more semantic constant names and updated resource keys.
- Updated `build.gradle.kts` to use the new `Main` class.
- Updated project version to `1.3.0-SNAPSHOT`.

## [1.2.0-SNAPSHOT] - 2023-11-13

### Added
- New dialog types: `MultiChoiceDialog`, `PasswordDialog`, `YesNoDialog`.
- `DialogTheme` system for customizable styling (colors, borders).
- `NavigationToolbarRenderer` for decoupled toolbar rendering.
- Javadoc documentation for public API (`SelectionMenu`, `TextLineQuestion`).

### Changed
- Refactored dialogs to use the Builder pattern for consistent construction.
- Moved `TextWrapper` to `org.dawciobiel.shelldialog.cli.util` package.
- Updated project version to `1.2.0-SNAPSHOT`.
- Refactored `NavigationToolbar` logic.
- Improved `README.md` with new examples and features list.

### Removed
- Redundant comments and unused code in examples and renderer.

## [1.1.0-SNAPSHOT] - 2023-10-27

### Added
- `QuestionDialog` for interactive text input.
- `Showable` interface for dialogs.
- Dialog result types: `Value`, `TextValue`, `IntegerValue`, `ErrorValue`.
- `DialogTextExample` demonstrating `QuestionDialog` usage.
- `NavigationToolbar` for improved navigation display.
- `gradle.properties` for Gradle configuration.

### Changed
- Renamed `MenuCLI` to `Menu` and moved to `cli.dialog` package.
- Moved `Messages` to `cli.i18n` package.
- Updated project version to `1.1.0-SNAPSHOT` in `build.gradle.kts` and `pom.xml`.
- Updated `README.md` with new version and features.
- Aligned `mainClass` in `pom.xml` with Gradle configuration.
- Updated `MenuUsageExample` to reflect new package structure.
- Updated `BorderLine` and `Arrow` classes to use new navigation constants.
- Updated `messages.properties` and `messages_pl.properties`.

### Removed
- Old `Menu` interface.
- Old `Navigation` class.

## [1.0.0-SNAPSHOT] - YYYY-MM-DD

### Added
- Initial project setup.
- `MenuCLI` class for creating interactive console menus (formerly `Menu`).
- `Menu` interface to support multiple implementations (CLI, GUI).
- Support for different border styles.
- Basic documentation files (`README.md`, `CHANGELOG.md`, `BUGS.md`).

### Changed
- Refactored `Menu` class to `MenuCLI` and moved to `menu.cli` package.
- Renamed `console` package to `cli` for better naming consistency.
- Updated `MenuUsageExample` to use `MenuCLI` and `Locale.of`.
- Removed unused `TerminalSize` class.
- Refactored `MenuCLI` class to improve readability and consistency.
- Renamed `drawMenu` to `printMenu` for consistent naming.
- Removed unnecessary section comments.
