# Another Life Counter

A Magic: The Gathering Commander life counter app for Android.


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
