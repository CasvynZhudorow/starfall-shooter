# Starfall Shooter

A native Android vertical shooter (2.5D) built with Kotlin + LibGDX + Ashley ECS.

## Features
- **2.5D Graphics**: 3D models with fixed orthographic camera
- **3 Playable Ships**: Interceptor (speed/piercing), Balanced (homing), Heavy (spread/nova)
- **Weapon Upgrades**: 5 levels per ship with distinct progression
- **Ultimate Abilities**: Unique per ship, charged by pickups
- **Stage-Based Progression**: 5 stages with unique bosses
- **Enemy Variety**: Straight shooters, spread shooters, multi-phase bosses
- **No Monetization**: Pure offline single-player experience
- **Min API 21**: Runs on Android 5.0+

## Tech Stack
- **Language**: Kotlin 1.9
- **Engine**: LibGDX 1.12 (LWJGL3 desktop, Android backend)
- **Architecture**: Ashley ECS (Entity Component System)
- **Build**: Gradle Kotlin DSL
- **CI/CD**: GitHub Actions (debug APK, release AAB, instrumented tests)

## Project Structure
```
android-shooter/
├── core/           # Shared game logic (ECS, systems, screens)
├── android/        # Android application module
├── desktop/        # Desktop launcher for testing
├── assets/prompts/ # AI asset generation prompts
├── .github/workflows/
│   ├── ci.yml      # PR checks: build, test, lint
│   └── release.yml # Tag push: signed AAB + GitHub Release
└── gradle/         # Version catalog
```

## Building

### Local (Desktop)
```bash
./gradlew :desktop:run
```

### Android Debug APK
```bash
./gradlew :android:assembleDebug
# Output: android/build/outputs/apk/debug/android-debug.apk
```

### Android Release AAB (requires keystore)
```bash
./gradlew :android:bundleRelease
# Output: android/build/outputs/bundle/release/android-release.aab
```

## GitHub Actions Setup

### Required Secrets (Repository Settings → Secrets → Actions)
| Secret | Description |
|--------|-------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore.jks |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

Generate keystore:
```bash
keytool -genkeypair -v -keystore keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias starfall
# Then base64 encode: base64 -w0 keystore.jks
```

### Workflows
- **CI** (`ci.yml`): Runs on every push/PR to main/develop
  - Builds debug APK
  - Runs unit tests (core module)
  - Runs Android Lint
  - Runs instrumented tests on API 21 & 30 emulators
- **Release** (`release.yml`): Runs on tag push (`v*`)
  - Builds signed release AAB
  - Creates GitHub Release with AAB artifact

## Controls
| Platform | Move | Ultimate | Pause |
|----------|------|----------|-------|
| Touch | Drag | Auto (when full) | — |
| Desktop | WASD / Arrows | Space / U | ESC / P |

## Gameplay
1. **Ship Select**: Choose one of 3 ships with different playstyles
2. **Stages 1-3**: 3 waves each, then boss
3. **Stages 4-5**: Boss rush
4. **Progression**: Weapon upgrades (pickups), Ultimate meter (pickups), Lives
5. **Game Over**: Retry from ship select, score resets

## AI Assets
All 3D models and textures are AI-generated. See `assets/prompts/models.md` for prompts and export pipeline.

Place generated `.g3db` models in `android/assets/models/`.

## License
MIT - Free to use, modify, distribute.# Force fresh CI run Mon 23 Nov 13:38:55 WIB 2026
