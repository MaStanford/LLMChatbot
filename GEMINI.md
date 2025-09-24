# Project Context: Android Chatbot

This document is the primary entry point for understanding the project. It provides a high-level overview and links to more detailed documentation.

## 1. Project Goal

The purpose of this application is to serve as a comprehensive demonstration of modern Android development practices for an interview with xAI. It is a fully functional LLM chatbot built from the ground up, touching on all major Jetpack libraries and adhering to Clean Architecture principles.

## 2. Core Features

-   **Real-time Streaming:** Fully asynchronous, real-time streaming of chat responses from multiple LLM backends, built with idiomatic Kotlin Flows and thread-safe concurrency patterns.
-   **Local Persistence:** Local persistence of chat history and sessions using Room.
-   **Configurable Context:** User-configurable settings, including API key management, theme selection, and a context length limit to manage conversation history sent to the LLM.
-   **Clean Navigation:** A navigable UI with separate screens for chat, session management, and settings.
-   **Graceful Error Handling:** Robust and graceful error handling for API failures, missing API keys, and streaming interruptions.
-   **Intelligent Context Management:** Sends only the most recent messages within the user-defined character limit, ensuring efficient and relevant API requests.
-   **Advanced Stream Parsing:** A robust JSON streaming parser that correctly handles long and complex LLM responses without truncation or data loss.
-   **Session Management:** Automatic and manual renaming of chat sessions.
-   **Markdown Rendering:** A custom-built Markdown text renderer for displaying formatted responses.
-   **Usability Features:** A "copy to clipboard" feature for all messages.

## 3. Documentation Index

For more detailed information, please refer to the documents in the `/docs` directory:

-   **[1_TECH_STACK.md](./docs/1_TECH_STACK.md):** A detailed list of all the libraries, frameworks, and tools used in this project and their purpose.
-   **[2_ARCHITECTURE.md](./docs/2_ARCHITECTURE.md):** An in-depth explanation of the Clean Architecture structure, layers, and data flow.
-   **[3_CODING_CONVENTIONS.md](./docs/3_CODING_CONVENTIONS.md):** Guidelines for code style, naming conventions, and established patterns in the codebase.
-   **[4_API_INTEGRATION.md](./docs/4_API_INTEGRATION.md):** A guide to how the application connects to external LLM APIs.
