# Another Life Counter

A Magic: The Gathering Commander life counter app for Android.

<img width="270" height="598" alt="Screenshot_20260904_175949" src="https://github.com/user-attachments/assets/272a5ed6-5e34-41e3-a28d-2984c552778f" />

<img width="270" height="598" alt="Screenshot_20260904_175928" src="https://github.com/user-attachments/assets/c2c4b59c-5d8c-4bdf-9eac-2cde53ae3d7c" />

<img width="270" height="598" alt="Screenshot_20260904_180004" src="https://github.com/user-attachments/assets/96162133-ae6a-4e00-988c-4b3475bb6f34" />


## Features

- **2-4 player support** with rotated panels for each player
- **Life tracking** with increment/decrement buttons
- **Commander damage tracking** between all players
- **Commander name search** via Scryfall API autocomplete
- **Starting player marker** randomly assigned at game start
- **Game history** — save and restore past games

## Tech Stack

- Kotlin
- Jetpack Compose (Material 3)
- Android Navigation Compose
- Room (local database for game history)
- OkHttp (Scryfall API)
- Kotlin Serialization

Requires Android SDK with compileSdk 37 and minSdk 26.
