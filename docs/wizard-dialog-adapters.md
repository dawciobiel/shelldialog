# WizardDialog Adapter Decision

## Goal

Decide whether `WizardDialog` should gain dedicated adapters for existing dialogs such as `FormDialog` and `FileDialog`, or remain a lightweight standalone step system.

## Decision

`WizardDialog` should remain a lightweight standalone step orchestrator.

Adapters may be added later, but only as thin, explicitly scoped integrations. They should not embed full dialog implementations inside a wizard step.

## Why

The current wizard model is simple and coherent:

- each step renders inside the wizard frame
- each step handles its own input and validation
- each step commits typed values into `WizardContext`

This keeps `WizardDialog` predictable and easy to extend with focused built-in steps such as:

- `WizardTextStep`
- `WizardPasswordStep`
- `WizardDirectoryStep`
- `WizardFileStep`
- `WizardSummaryStep`

Embedding existing dialogs directly would work against that model.

## Rejected Direction: Full Dialog Embedding

Treating `FormDialog` or `FileDialog` as wizard steps sounds attractive, but it introduces several problems:

- nested navigation models (`Back`/`Next` versus dialog-specific controls)
- duplicated rendering responsibilities
- unclear ownership of validation and error display
- pressure to expose more internal dialog state just to support wizard integration
- increased maintenance cost when standalone dialogs evolve

In practice, this would push `WizardDialog` toward becoming a second UI framework instead of a small orchestration layer.

## Accepted Direction: Thin Integrations Only

If integrations are added later, they should stay narrow and explicit.

Good examples:

- a dedicated wizard step that reuses validation concepts from `FormDialog`
- a dedicated wizard step that accepts a `Path` and mirrors a subset of `FileDialog` validation rules
- helper factories that build wizard steps from common presets

Bad examples:

- embedding a full `FileDialog` browser inside a wizard step
- wrapping `FormDialog.show()` as a nested modal inside `WizardDialog`
- sharing mutable rendering internals between dialogs and wizard steps

## Practical Rule

Prefer adding a new `Wizard*Step` when:

- the interaction fits the existing single-step wizard model
- the value committed to `WizardContext` is simple and typed
- the UX can remain consistent with current wizard navigation

Consider a thin adapter only when:

- the reuse is substantial
- the API stays small
- the step does not need access to standalone dialog internals

## Current Recommendation

For the near term:

- keep extending `WizardDialog` through focused built-in steps when needed
- avoid full adapters for `FormDialog` and `FileDialog`
- revisit adapter support only if a repeated integration need appears in real usage
