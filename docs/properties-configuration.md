# Properties Configuration Decision

## Goal

Decide whether ShellDialog configuration should also be loadable from `.properties` files in addition to Java builders.

## Decision

ShellDialog should keep Java builders as the primary and recommended configuration model.

`.properties` support may be added later only for narrow, presentation-oriented cases. It should not become a parallel way to define full dialog behavior.

## Why

The current builder API is one of the library's strengths:

- it is explicit
- it is type-safe
- it composes naturally with validators, mappers, and callbacks
- it keeps configuration close to the runtime code that uses it

Trying to mirror all of that in `.properties` would quickly create a second, weaker configuration system.

## Rejected Direction: Full Dialog Definitions in `.properties`

Using `.properties` files to define complete dialogs sounds flexible, but it breaks down once dialogs become richer.

Problem areas include:

- validators
- typed result mapping
- dialog option lists
- file filters and shortcut paths
- wizard step composition
- conditional flow and application-specific callbacks

These behaviors fit Java code much better than string-based configuration.

## Accepted Direction: Narrow Optional Support

If `.properties` support is added later, it should stay limited to static presentation data.

Reasonable candidates:

- titles
- body text
- simple button labels
- basic toolbar labels
- theme presets or named style references

Possible use cases:

- loading copy for `MessageDialog`
- populating simple text content for informational screens
- centralizing static user-facing strings outside application code

## Practical Rule

Use Java builders for:

- structure
- behavior
- validation
- typed results
- dynamic choices
- wizard composition

Use `.properties` only for:

- static text
- shallow presentation settings
- optional convenience layers on top of builders

## Current Recommendation

For the near term:

- do not implement full `.properties`-driven dialog definitions
- keep builders as the public API center of gravity
- revisit narrow `.properties` helpers only if a concrete, repetitive use case appears
