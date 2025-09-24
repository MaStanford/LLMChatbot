# Architecture: Clean Architecture with Jetpack

This project follows the principles of Clean Architecture, adapted for a modern Android application using Jetpack components. The goal is to create a separation of concerns, making the codebase modular, scalable, testable, and maintainable.

## 1. Layers

The architecture is divided into three main layers:

### a. UI Layer (app module)

-   **Purpose:** To display application data on the screen and handle user interaction.
-   **Components:**
    -   **Views (Compose):** UI elements are built declaratively using Jetpack Compose.
    -   **ViewModels:** Act as state holders for the UI. They expose data as observable streams (Flows) and handle user actions. ViewModels are lifecycle-aware and survive configuration changes.
    -   **Navigation:** A single-activity architecture managed by Jetpack Navigation for Compose.

### b. Domain Layer (Future)

-   **Purpose:** To contain the core business logic of the application. This layer is independent of any framework-specific details.
-   **Components:**
    -   **Use Cases (Interactors):** These classes encapsulate a single, specific business rule. For example, `SendMessageUseCase` or `GetChatHistoryUseCase`.
    -   **Models:** Plain Kotlin data classes that represent the core entities of the application (e.g., `ChatMessage`, `ChatSession`).

*(Note: For this project's initial scope, some business logic may reside in the ViewModels or Repositories. A dedicated domain layer will be added as the app's complexity grows.)*

### c. Data Layer (app module)

-   **Purpose:** To abstract the sources of data and provide a clean API for the rest of the app.
-   **Components:**
    -   **Repositories:** The single source of truth for application data. They abstract away the origin of the data (network, database, or cache) and provide a clean interface for the domain or UI layer to access it. (e.g., `SettingsRepository`, `ChatRepository`).
    -   **Data Sources:**
        -   **Remote:** Handles communication with the LLM APIs (e.g., OpenAI, Gemini). This will be implemented using a library like Retrofit or Ktor.
        -   **Local:** Manages local data persistence.
            -   **Room:** A local SQLite database for storing chat history and sessions.
            -   **DataStore:** A key-value store for simple user preferences (e.g., theme, selected model).

## 2. Key Libraries & Concepts

-   **Dependency Injection (Hilt):** Hilt is used to provide dependencies throughout the app. It simplifies DI by handling the boilerplate of manual injection, making the code cleaner and more testable.
-   **Coroutines & Flow:** All asynchronous operations (network requests, database queries) are handled using Kotlin Coroutines. Data is exposed from the data and domain layers to the UI layer using Flow, creating a reactive data stream.
-   **Single Source of Truth:** The Repositories are designed to be the single source of truth for any given piece of data, ensuring consistency across the application.
