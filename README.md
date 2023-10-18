# ComposeChatExample - Client

Requirements for the application to be developed:
- Authorization/Registration
- Creating a user chat
- Exchange of messages using websockets

The project used:
- Ktor websockets
- Ktor client (instead of Retrofit)
- Jetpack Compose
- Hilt
- Coroutines/Flow
- Event providers (Channels)
- Shared Preferences

  Also used mongoDB and ktor server side
  https://github.com/IlyaSibogatov/ComposeChatExample-server

  # Onboard screen
  Screen for logging in or creating an account, if no account is available.
  
  ![onboarding_screen](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/010ace1c-46e0-46a8-b4c1-4c7c260fd774)

  # Chat list screen
  Display of the list of available rooms, creation of public and private chats, search through the list is implemented.
  
  ![empty_chat_list_screen](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/a324496e-3b09-4453-ba2f-149aef40eb99)
  ![create_chat_dialog](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/d9363a0c-ed13-4171-8e33-90fffaaef6bc)
  ![chat_list_screen_with_room](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/bc98f421-e731-4855-addd-d5b3c4df739a)

  # Chat detail screen
  Realised messaging using websocket's, possibility to delete or edit own messages.
  
  ![receive_messages](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/6add33af-cbfa-44aa-b76e-847090bc0f57)
  ![long_click_own_message](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/36a43552-ff18-4870-a0f2-84b570d13de0)
  ![edit_message_process](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/09c5ac9e-d028-4126-80b5-943001c0171a)
  ![message_was_edited](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/f8cd7954-852e-47b3-a4c3-9ab08b09b6ed)

  # Profile screen
  Display user's online status, self information, friends list.
  
  ![user_profile_screen](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/b9b9e2f8-4191-429a-bdee-7245b448157b)

  # Settings screen
  Currently implemented:
- Language change. Available in two languages - Russian - English
- Themes change. System, Light, Dark
- Communication with technical support (Send email)
- User logout from the account
  
  ![settings_screen](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/722c935b-7d13-4e10-a745-4c21ed0284b7)
  ![ru_language_selected](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/eba5b0f6-18b5-4317-bb66-4e2c00051be8)
