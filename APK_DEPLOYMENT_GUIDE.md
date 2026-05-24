# Lapor Tasik - Android APK Deployment Guide

## 📱 Application Information

- **App Name:** Lapor Tasik
- **Developer:** By Rangga
- **Version:** 1.0.0
- **Package Name:** com.ranggazeedrn.laporTaskik
- **Min SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 14 (API 34)

---

## 🚀 Build APK Instructions

### Method 1: Using Build Script (Recommended)

```bash
# Navigate to project root
cd apps

# Make script executable
chmod +x build-apk.sh

# Run build script
./build-apk.sh
```

The APK will be generated at: `app/build/outputs/apk/release/app-release.apk`

### Method 2: Using Gradle Commands

```bash
# Clean build
./gradlew clean

# Build Release APK
./gradlew assembleRelease
```

### Method 3: Using Android Studio

1. Open project in Android Studio
2. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. APK will be generated in `app/build/outputs/apk/release/`

---

## 📦 APK Output Location

After successful build, the APK file will be located at:

```
apps/app/build/outputs/apk/release/app-release.apk
```

---

## 📥 Installation on Device

### On Android Device/Emulator

```bash
# Install APK on connected device
adb install app/build/outputs/apk/release/app-release.apk

# Or force install over existing app
adb install -r app/build/outputs/apk/release/app-release.apk
```

### Manual Installation

1. Transfer `app-release.apk` to Android device
2. Open file manager on device
3. Locate and tap the APK file
4. Allow installation from unknown sources (if needed)
5. Tap **Install**

---

## 🔍 Build Configuration Details

### Signing Configuration

The APK is signed with the following configuration:
- **Keystore:** `release-key.jks` (in project root)
- **Key Alias:** `laporTaskik`
- **Validity:** Configured for release distribution

### Build Types

#### Debug APK
```bash
./gradlew assembleDebug
```
- Not minified
- Full debugging support
- Located at: `app/build/outputs/apk/debug/app-debug.apk`

#### Release APK
```bash
./gradlew assembleRelease
```
- Minified and optimized
- ProGuard rules applied
- Signed for distribution
- Located at: `app/build/outputs/apk/release/app-release.apk`

---

## ⚙️ Dependencies

### Core Libraries
- Jetpack Compose (UI Framework)
- Material Design 3
- Androidx Core KTX
- Lifecycle Runtime

### Versions
- Compose BOM: 2023.10.01
- Material3: 1.1.1
- Kotlin: 1.9.0
- Gradle: 8.1.0

---

## 🧪 Testing Before Release

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Build with tests
./gradlew assembleRelease
```

---

## 📊 APK Size Information

Expected APK size: ~2-3 MB (depends on dependencies added)

To check APK size details:
```bash
./gradlew bundleRelease
```

---

## 🔐 Security Notes

- APK is signed with release keystore
- ProGuard is enabled for code obfuscation
- Resource shrinking is enabled
- Package name: `com.ranggazeedrn.laporTaskik`

---

## 📝 Version Management

To update version for next release, edit `app/build.gradle.kts`:

```kotlin
versionCode = 2  // Increment by 1
versionName = "1.1.0"  // Update version
```

---

## 🆘 Troubleshooting

### Build fails with "Gradle not found"
```bash
# Make gradlew executable
chmod +x gradlew

# Retry build
./gradlew assembleRelease
```

### APK installation fails on device
```bash
# Uninstall previous version
adb uninstall com.ranggazeedrn.laporTaskik

# Then install new APK
adb install app/build/outputs/apk/release/app-release.apk
```

### Build takes too long
- Increase heap size: `export GRADLE_OPTS="-Xmx2048m"`
- Use `--parallel` flag: `./gradlew assembleRelease --parallel`

---

## 📤 Distribution

The compiled APK is ready for:
- ✅ Google Play Store submission
- ✅ Manual distribution
- ✅ Beta testing
- ✅ Internal deployment

---

## 📞 Support

For issues or questions, contact: Rangga

---

**Built with ❤️ in Kotlin | Lapor Tasik v1.0.0**
