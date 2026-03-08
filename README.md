# OpenSourceBikeShare Android

Native Android client for [OpenSourceBikeShare](https://github.com/cyklokoalicia/OpenSourceBikeShare) — the world's first low-cost and open source bike sharing system.

## Features

### User Features
- 🗺️ **Map** — Interactive map with bike stand markers and geolocation
- 🚲 **Rent & Return** — Rent bikes from stands and return them with optional notes
- 📷 **QR Scanner** — Scan QR codes on bikes and stands for quick rent/return
- 💰 **Credit System** — View balance and redeem coupons
- 👤 **Profile** — View limits, change city, manage account

### Admin Features
- 📍 **Manage Stands** — View stands, delete notes
- 🚲 **Manage Bikes** — View details, set lock codes, force rent/return, revert state
- 👥 **Manage Users** — View/edit users, add credits
- 🎫 **Manage Coupons** — List, generate, mark as sold
- 📊 **Reports** — Daily usage, user reports by year, inactive bikes

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp + Moshi
- **Maps**: OpenStreetMap via osmdroid (no API key required)
- **Camera**: CameraX + ML Kit (QR scanning)
- **Auth**: JWT with automatic token refresh
- **Min SDK**: 26 (Android 8.0)

## Prerequisites (CLI build without Android Studio)

1. **JDK 17** — Install OpenJDK 17:
   ```bash
   # Ubuntu/Debian
   sudo apt install openjdk-17-jdk-headless
   # macOS (Homebrew)
   brew install openjdk@17
   ```
2. **Android SDK** — Install command-line tools:
   ```bash
   # Download Android command-line tools
   wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
   unzip commandlinetools-linux-*_latest.zip -d $HOME/android-sdk/cmdline-tools/latest
   
   # Set environment variables (add to ~/.bashrc)
   export ANDROID_HOME=$HOME/android-sdk
   export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH
   
   # Accept licenses and install required components
   sdkmanager --licenses
   sdkmanager "platforms;android-35" "build-tools;35.0.0" "platform-tools"
   ```

## Setup

1. Clone the repository
2. Open in Android Studio (Hedgehog or later) **or** build from command line with Gradle
3. Configure the API base URL in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"https://your-server.com/api/v1/\"")
   ```
4. Build and run:
   ```bash
   # CLI build (no Android Studio needed)
   ./gradlew assembleDebug
   # Install on connected device
   ./gradlew installDebug
   ```

> **Note:** This project uses OpenStreetMap (osmdroid) — no Google Maps API key is required.

## API

This app uses the OpenSourceBikeShare REST API v1 with JWT authentication. See [openapi.yaml](https://github.com/cyklokoalicia/OpenSourceBikeShare/blob/master/openapi.yaml) for the full API specification.

## Localization

The app supports three languages:
- 🇬🇧 English (default)
- 🇺🇦 Ukrainian
- 🇸🇰 Slovak

## License

GPL-3.0 — Same as the parent project.
