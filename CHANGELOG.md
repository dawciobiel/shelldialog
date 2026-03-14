# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
