# Todo List

- [x] Add dedicated user documentation for `NavigationToolbar` and `NavigationToolbarRenderer`.
- [x] Add `SpinnerDialog` for background tasks with indeterminate duration.
- [x] Add `ProgressDialog` for background tasks with known progress.
- [x] Implement live filtering (search) for `SingleChoiceDialog` and `MultiChoiceDialog`.
- [x] Add interaction-level tests that simulate full keyboard flows for dialogs.
- [x] Implement specialized `InputValidator` and `PasswordValidator` with built-in common validation rules.
- [x] Improve `FileDialog` with smart sorting (folders first) and navigation shortcuts (Home/End/F5).
- [x] Modernize the codebase with Java 21 features (Sequenced Collections, Math.clamp).
- [x] Implement `MessageDialog` for showing simple messages with an OK button.
- [x] Implement `FormDialog` to allow multiple input fields on a single screen.
- [x] Add `WizardDialog` support for multi-step dialog sequences with navigation.
- [ ] Extend `WizardDialog` with additional built-in step types (e.g. password and file/directory selection).
- [ ] Decide whether `WizardDialog` should gain dedicated adapters for existing dialogs or remain a lightweight standalone step system.
- [ ] Decide whether dialog configuration should also be loadable from `.properties` files in addition to Java builders.
