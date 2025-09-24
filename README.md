# Android Chatbot

This is a fully functional, modern Android chatbot application built from the ground up as a comprehensive demonstration of contemporary Android development practices. It showcases a deep understanding of Jetpack libraries, Clean Architecture, and advanced concepts like asynchronous data streaming.

## Key Features

-   **Real-time Streaming:** Fully asynchronous, real-time streaming of chat responses from multiple LLM backends (Gemini, OpenAI, and Xai), built with idiomatic Kotlin Flows and thread-safe concurrency patterns.
-   **Local Persistence:** Local persistence of chat history and sessions using Room.
-   **Configurable Context:** User-configurable settings, including API key management, theme selection, and a context length limit to manage conversation history sent to the LLM.
-   **Clean Navigation:** A navigable UI with separate screens for chat, session management, and settings, built with Jetpack Navigation.
-   **Graceful Error Handling:** Robust and graceful error handling for API failures, missing API keys, and streaming interruptions.
-   **Intelligent Context Management:** Sends only the most recent messages within the user-defined character limit, ensuring efficient and relevant API requests.
-   **Advanced Stream Parsing:** A robust JSON streaming parser that correctly handles long and complex LLM responses without truncation or data loss.
-   **Session Management:** Automatic and manual renaming of chat sessions.
-   **Markdown Rendering:** A custom-built Markdown text renderer for displaying formatted responses.
-   **Usability Features:** A "copy to clipboard" feature for all messages.

## Tech Stack & Architecture

This project is built with a modern tech stack and follows the principles of Clean Architecture to create a modular, scalable, and maintainable codebase.

-   **UI:** Jetpack Compose (with Material 3)
-   **Architecture:** Clean Architecture (UI, Domain, Data layers)
-   **Asynchronous Programming:** Kotlin Coroutines & Flow
-   **Dependency Injection:** Hilt
-   **Database:** Room
-   **Settings:** DataStore
-   **Navigation:** Jetpack Navigation
-   **Networking:** OkHttp & Ktor

For a more detailed breakdown of the architecture and technical decisions, please see the [full documentation](./docs).

## Getting Started

1.  Clone the repository:
    ```bash
    git clone https://github.com/mastanford/llmchatbot.git
    ```
2.  Open the project in Android Studio.
3.  The project uses the Gradle build system and should sync automatically.
4.  To run the app, you will need to provide your own API keys for the LLM services you wish to use. These can be set in the in-app settings screen.

## Screenshots

*(Placeholder for app screenshots)*

| Chat Screen | Session Management | Settings |
| :---: | :---: | :---: |
| *Screenshot 1* | *Screenshot 2* | *Screenshot 3* |
