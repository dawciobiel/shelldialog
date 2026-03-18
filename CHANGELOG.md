# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachanglog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
