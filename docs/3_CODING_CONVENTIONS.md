# 3. Coding Conventions & Style Guide

This document outlines the established coding conventions and patterns to ensure consistency and maintainability.

## General Principles

-   **Language:** The project is written entirely in Kotlin.
-   **Style:** Follows the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) recommended by JetBrains.
-   **Immutability:** Prefer `val` over `var` wherever possible. Use immutable data structures (e.g., `List` over `MutableList`) for public APIs of classes.

## Naming Conventions

-   **Classes:** PascalCase (e.g., `ChatViewModel`).
-   **Functions & Variables:** camelCase (e.g., `sendMessage`, `userInput`).
-   **Composables:** PascalCase and should be nouns or descriptive phrases (e.g., `ChatScreen`, `MessageInput`).
-   **ViewModels:** Suffix with `ViewModel` (e.g., `SettingsViewModel`).
-   **Repositories:** Suffix with `Repository` (e.g., `ChatRepository`).
-   **DAOs:** Suffix with `Dao` (e.g., `ChatMessageDao`).
-   **API Services:** Suffix with `ApiService` (e.g., `GeminiApiService`).

## Architectural Patterns

-   **MVVM (Model-View-ViewModel):** The UI layer is built using this pattern.
    -   **View (Composable):** Observes state from the ViewModel and calls its functions to handle user events.
    -   **ViewModel:** Holds and exposes UI state as `StateFlow` or `State`. Contains the business logic for the screen.
-   **Repository Pattern:** The data layer uses repositories to abstract data sources. ViewModels should only interact with repositories, never directly with DAOs or API services.
-   **Dependency Injection:** Hilt is used for DI. Dependencies are provided in the `AppModule.kt` file and injected into constructors using `@Inject`.

## UI (Jetpack Compose)

-   **State Hoisting:** State is hoisted to the lowest common ancestor. For screen-level state, the ViewModel is the source of truth. For component-level state (e.g., whether a dropdown is expanded), `remember` and `mutableStateOf` are used within the composable.
-   **Previews:** All major composables should have a `@Preview` function for easy visualization in Android Studio.

## Custom Composables

-   **`MarkdownText`:** Located in `ui/components`, this composable is designed to render a subset of Markdown syntax. It is built using `buildAnnotatedString` and a custom iterative parser to handle bold, italics, blockquotes, and escaped characters. It serves as an example of advanced text styling in Compose.
