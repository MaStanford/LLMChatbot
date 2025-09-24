# 1. Tech Stack & Dependencies

This document details the key libraries, frameworks, and tools used in this project.

## Core Android & Jetpack

-   **Jetpack Compose:** The entire UI is built using this modern, declarative UI toolkit.
-   **ViewModel:** Used for managing UI-related data in a lifecycle-conscious way.
-   **Room:** For local persistence of chat messages and sessions in a SQLite database.
-   **DataStore (Preferences):** For storing simple key-value data, such as user settings and API keys.
-   **Hilt:** For dependency injection, managing the creation and provision of dependencies throughout the app.
-   **Jetpack Navigation (Compose):** For navigating between different screens in the app.

## Asynchronous Programming

-   **Kotlin Coroutines & Flow:** Used for all asynchronous operations, including database access and network requests. This allows for non-blocking code and reactive data streams.

## Networking

-   **Ktor Client:** A modern, asynchronous HTTP client used to make requests to the LLM APIs.
-   **Kotlinx Serialization:** For parsing JSON data from the network into Kotlin data classes.

## Architecture

-   **Clean Architecture:** The project is structured into distinct layers (UI, Data) to ensure a separation of concerns, making the codebase modular, testable, and maintainable.
