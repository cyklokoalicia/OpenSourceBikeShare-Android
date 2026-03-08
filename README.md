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
- **Maps**: Google Maps Compose
- **Camera**: CameraX + ML Kit (QR scanning)
- **Auth**: JWT with automatic token refresh
- **Min SDK**: 26 (Android 8.0)

## Setup

1. Clone the repository
2. Open in Android Studio (Hedgehog or later)
3. Add your Google Maps API key to `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
4. Configure the API base URL in `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "API_BASE_URL", "\"https://your-server.com/api/v1/\"")
   ```
5. Build and run

## API

This app uses the OpenSourceBikeShare REST API v1 with JWT authentication. See [openapi.yaml](https://github.com/cyklokoalicia/OpenSourceBikeShare/blob/master/openapi.yaml) for the full API specification.

## Localization

The app supports three languages:
- 🇬🇧 English (default)
- 🇺🇦 Ukrainian
- 🇸🇰 Slovak

## License

GPL-3.0 — Same as the parent project.
