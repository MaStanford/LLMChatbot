# Feature Roadmap

This document outlines the planned features for the Chatbot project, tracking their status and defining the development path.

---

### Core Functionality

| Feature             | Status      | Description                                                                                             |
| ------------------- | ----------- | ------------------------------------------------------------------------------------------------------- |
| # Feature Roadmap

This document outlines the planned features for the Chatbot project, tracking their status.

---

### Core Functionality

| Feature                 | Status      | Description                                                                                              |
| ----------------------- | ----------- | -------------------------------------------------------------------------------------------------------- |
| **Streaming Responses**     | **Completed** | The app successfully streams responses token-by-token from live LLM APIs.                                |
| **Chat History**          | **Completed** | Chat history is saved to and loaded from the local Room database.                                        |
| **Chat Sessions**         | **Completed** | Users can create, view, select, delete, and rename chat sessions. Sessions are automatically named.      |
| **Settings**              | **Completed** | A functional settings screen allows for theme selection and API key management for all integrated LLMs. |
| **Error Handling**        | **Completed** | The app gracefully handles missing API keys and network/API errors, displaying clear messages to the user. |

---

### User Experience & UI

| Feature                 | Status      | Description                                                                                              |
| ----------------------- | ----------- | -------------------------------------------------------------------------------------------------------- |
| **Core UI**               | **Completed** | A functional `ChatScreen` with a message list and input field is implemented.                            |
| **LLM Selection**         | **Completed** | A dropdown in the `ChatScreen` allows for real-time switching between configured LLM backends.           |
| **Copy to Clipboard**     | **Completed** | A copy button is available on each chat message.                                                         |
| **Light/Dark Theme**      | **Completed** | Users can switch between light, dark, and system default themes in the settings.                         |
| **Markdown Rendering**    | **Completed** | A custom composable renders Markdown, including bold, italics, blockquotes, and newlines. |
| **Share Conversation**    | **To Do**     | Share the content of a chat session with other apps.                                                     |

---

### Advanced Features (Future Work)

| Feature              | Status    | Description                                                              |
| -------------------- | --------- | ------------------------------------------------------------------------ |
| **Text-to-Speech**     | **To Do** | Read the assistant's response aloud.                                     |
| **Speech-to-Text**     | **To Do** | Allow the user to speak to the chatbot instead of typing.                  |
| **Image Uploading**    | **To Do** | Upload images and have the chatbot understand them (multi-modal).        |
| **Web Search**         | **To Do** | Allow the chatbot to search the web to answer questions.                   |
| **Function Calling**   | **To Do** | Allow the chatbot to call external functions or APIs to perform actions. |
| **User Feedback**      | **To Do** | Allow users to rate responses or provide feedback.                         |
| **Authentication**     | **To Do** | Allow users to log in and save their chat history to the cloud.            |

---

## Project Status

The project is now in a feature-complete and stable state, successfully meeting all initial goals. It serves as a strong demonstration of modern Android architecture and best practices.

| **Chat History**      | **In Progress** | Store and display previous messages in a conversation. Room DB, DAO, and ViewModel are fully integrated. |
| **Chat Sessions**     | **In Progress** | App creates and persists a session ID. Next step is to allow creating and switching between sessions. |
| **Settings**          | **In Progress** | DataStore is set up. A basic settings screen exists. Next step is to add actual settings.            |

---

### User Experience & UI

| Feature                | Status      | Description                                                                        |
| ---------------------- | ----------- | ---------------------------------------------------------------------------------- |
| **Core UI**            | **Completed** | A functional `ChatScreen` with a message list and input field is implemented.      |
| **Markdown Rendering**   | **To Do**   | Display formatted text from the LLM, such as bold, italics, lists, and code blocks. |
| **Copy to Clipboard**    | **To Do**   | Add a button to easily copy the assistant's response.                                |
| **Share Conversation**   | **To Do**   | Share the content of a chat session with other apps.                                 |
| **Light/Dark Theme**     | **In Progress** | Allow users to switch between light, dark, and system default themes. DataStore is set up. |

---

### Advanced Features

| Feature              | Status    | Description                                                              |
| -------------------- | --------- | ------------------------------------------------------------------------ |
| **Text-to-Speech**     | **To Do** | Read the assistant's response aloud.                                     |
| **Speech-to-Text**     | **To Do** | Allow the user to speak to the chatbot instead of typing.                  |
| **Image Uploading**    | **To Do** | Upload images and have the chatbot understand them (multi-modal).        |
| **Web Search**         | **To Do** | Allow the chatbot to search the web to answer questions.                   |
| **Function Calling**   | **To Do** | Allow the chatbot to call external functions or APIs to perform actions. |
| **User Feedback**      | **To Do** | Allow users to rate responses or provide feedback.                         |
| **Authentication**     | **To Do** | Allow users to log in and save their chat history to the cloud.            |

---

## Next Steps

1.  **Implement Remote Data Source:** Create a `ChatApiService` to handle communication with an LLM API.
2.  **Implement Streaming Logic:** Update the `ChatViewModel` to call the API and handle the streaming response.
3.  **Update UI for Streaming:** Modify the `ChatScreen` to display the streaming response in real-time.
4.  **Implement Settings:** Add UI elements to the `SettingsScreen` to allow users to change the theme and selected LLM.
