# Emoji Dodge

## Project Overview
Emoji Dodge is a small arcade-style mobile game where the player controls a character at the bottom of the screen and must avoid falling emoji obstacles. The goal is to survive as long as possible while the speed and spawn rate increase over time. The game includes pause/play controls, exit confirmation, high-score persistence, power-ups, and reactive animations.

## Tech Stack
- Kotlin
- Jetpack Compose (UI)
- Navigation Compose (type-safe destinations)
- SharedPreferences (high score persistence)

## Screenshots
Home Screen
![Home Screen](/Users/apple/AndroidStudioProjects/AIproduct/1.png)

Game Screen
![Game Screen](/Users/apple/AndroidStudioProjects/AIproduct/2.png)

Game Over Screen
![Game Over Screen](/Users/apple/AndroidStudioProjects/AIproduct/3.png)

Powerups
![Powerups](/Users/apple/AndroidStudioProjects/AIproduct/4.png)

## How To Run
1. Open the project in Android Studio.
2. Sync Gradle if prompted.
3. Run the `app` configuration on an emulator or device.

Optional command line build:
```bash
./gradlew :app:installDebug
```

## Development Approach
- Split the UI into independent screens: Home, Game, and Game Over, wired with type-safe Navigation Compose.
- Implemented a frame-based game loop using `withFrameNanos` and a coroutine-based obstacle spawner.
- Added collision detection with arcade-style hit feedback (shake, flash, and explosion).
- Stored and displayed high score using SharedPreferences and celebratory animations when surpassed.
- Added collectible power-ups for bonus points and temporary invincibility.
- Applied arcade-inspired typography, gradients, and motion to make the UI feel game-like.
