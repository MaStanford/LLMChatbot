# 4. API Integration Guide

This document explains how the application connects to and interacts with external Large Language Model (LLM) APIs.

## 1. Core Components

-   **`ChatApiService` (Interface):** Located in `data/remote`, this interface defines the contract for all LLM API services. It has a single function, `getChatCompletionStream`, which takes a `ChatRequest` and returns a `Flow<ChatResponseChunk>`.
-   **API Service Implementations:**
    -   `GeminiApiService.kt`: The implementation for Google's Gemini API.
    -   `OpenAiApiService.kt`: The implementation for OpenAI's GPT models.
    -   `XaiApiService.kt`: A production-ready (but placeholder) implementation for the xAI API.
-   **`ChatRepository`:** This repository is the single point of contact for the `ChatViewModel`. It abstracts away which API service is being used by reading the user's selected model from the `SettingsRepository` and delegating the API call to the appropriate, Hilt-injected service.
-   **Ktor `HttpClient`:** A single, shared instance of the Ktor HTTP client is provided by Hilt (`AppModule.kt`) and used by all API services to make network requests.

## 2. Data Flow for a Message

1.  **User Input:** The user types a message in the `ChatScreen` and taps "Send".
2.  **ViewModel:** The `ChatScreen` calls the `sendMessage()` function on the `ChatViewModel`.
3.  **API Key Check:** The `ChatViewModel` reactively determines the correct API key for the currently selected model. Inside `sendMessage`, it suspends and waits for the most current key using `.first()` on the appropriate `Flow` from the `SettingsRepository`. If the key is blank, it displays an error and stops.
4.  **Repository Call:** The `ChatViewModel` builds a `ChatRequest` object (containing the message history and model name) and passes it to the `ChatRepository`.
5.  **Service Selection:** The `ChatRepository` determines which `ChatApiService` to use based on the selected model.
6.  **Network Request:** The chosen `ChatApiService` uses the Ktor `HttpClient` to make a streaming POST request to the appropriate API endpoint. It also maps the internal message roles (e.g., "assistant") to the API-specific roles (e.g., "model") as needed.
7.  **Streaming Response:** The API service parses the streaming response line-by-line, emitting `ChatResponseChunk` objects.
8.  **ViewModel Update:** The `ChatViewModel` collects the `Flow` of chunks. For each chunk, it updates the content of the assistant's message in the Room database.
9.  **UI Update:** The `ChatScreen` is observing a `StateFlow` of messages that is reactively connected to the database via a `flatMapLatest` operator. As the database is updated with new tokens, the UI recomposes automatically, creating the "live typing" effect.

## 3. Error Handling

-   **API Key Errors:** Handled proactively in the `ChatViewModel` before the network request is made.
-   **Network & HTTP Errors:** Each `ChatApiService` wraps its network calls in a `try-catch` block. If an exception occurs, a `ChatResponseChunk` with an `error` message is emitted. The services also parse the response body for API-specific error JSON and emit those as error chunks. All errors are then displayed in the UI.
