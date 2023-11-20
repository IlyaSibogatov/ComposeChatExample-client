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
  
  ![chat_list_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/57246da9-dd55-4387-82ac-c7bafd64f9e3)

  # Chat detail screen
  Realised messaging using websocket's, possibility to delete or edit own messages.
  
  ![chat_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/d1f23ab1-2db3-46d4-aeb9-1765ab4d359e)

  # Profile screen
  Display user's online status, self information, friends list.
  
  ![profile_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/3f44eda1-c76b-475d-9d29-67abe2b2816d)

  # Upload media screen`s

  ![upload_image_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/3a69cdb7-8f12-4bb6-8018-c096c34cc9eb)
  ![upload_video_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/2661e6f8-025c-42e2-8f68-3c26ce8e99a2)


  # Video player screen
  
  ![video_player_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/e4b3e783-e4d3-4a43-99db-0f43b6d7b925)


  # Notification screen
  
  ![notification_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/a93f44a1-5811-4995-9585-0f6e660d55e8)


  # Settings screen
  Currently implemented:
- Language change. Available in two languages - Russian - English
- Themes change. System, Light, Dark
- Communication with technical support (Send email)
- Remove an account
- Switch notification enabled
- User logout from the account.
  

  ![settings_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/0d211e93-6e64-4395-9060-26c9752c9a69)
  ![settings_dark_theme_on](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/a24adfb8-471a-4b5b-ac35-790154f1f0e3)
  ![settings_ru_page](https://github.com/IlyaSibogatov/ComposeChatExample-client/assets/95710591/eb18e6e9-9cc6-4fe2-a275-f2762c1475b6)
