# FormPic — Indian Passport & Exam Photo Maker (Under 50 KB)

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/On--Device%20AI-Google%20ML%20Kit-FF6F00?logo=google&logoColor=white)](https://developers.google.com/ml-kit)
[![Target Size](https://img.shields.io/badge/Target-Under%2050%20KB%20Guaranteed-10B981)](#exact-kb-compression-engine)

FormPic is a modern, privacy-first, on-device Android application designed specifically for Indian applicants filling out government exam forms (SSC, UPSC, IBPS, NEET, RRB), passports (Passport Seva / MEA), visas, and university admission portals.

The user mental model is straightforward:
> **TAKE / CHOOSE PHOTO ➔ SELECT REQUIREMENT (e.g. Under 50 KB) ➔ DONE!**

---

## 📸 Key Features

1. **Guaranteed Exact KB Compression Engine**
   - 1-Tap Presets: **Under 25 KB**, **Under 50 KB**, **Under 75 KB**, **Under 100 KB**, and **Custom KB**.
   - Intelligent binary search over JPEG compression quality + adaptive downsampling.
   - Strict byte verification: Output byte count is **guaranteed** to satisfy `finalBytes <= targetMaxKb * 1024L`. Never face portal rejections again.
2. **100% On-Device AI Privacy**
   - Uses Google ML Kit Face Detection & Selfie Segmentation.
   - Zero photos leave the smartphone. No cloud servers, no remote tracking of images.
3. **Studio-Clean Pure White Background**
   - Automatically segments subject foreground and replaces cluttered or uneven backgrounds with pure `#FFFFFF` white.
   - Features soft anti-aliased edge blending for hair and shoulders.
   - Includes optional manual Touch-Up Brush (Erase/Restore) for fine-tuning.
4. **Official Indian Exam & Passport Presets**
   - **Indian Passport (Passport Seva / MEA):** 35 × 45 mm (Ratio 7:9), pure white background, under 100 KB.
   - **SSC (CGL, CHSL, MTS, GD):** 20 KB – 50 KB, 3.5 × 4.5 cm.
   - **UPSC (Civil Services, NDA, CDS):** 20 KB – 300 KB, min 350 × 350 px, 3/4th face coverage.
   - **IBPS & SBI (Bank PO & Clerk):** 200 × 230 px, 20 KB – 50 KB.
   - **NEET UG (NTA):** 10 KB – 200 KB, 80% face coverage.
   - **Railway RRB (NTPC, ALP, Group D):** 35 × 45 mm, 20 KB – 50 KB.
   - **PAN Card (NSDL / UTIITSL):** 2.5 × 3.5 cm, under 50 KB.
5. **CameraX Passport Oval Viewfinder**
   - Real-time oval face positioning guide with eye-level and head-top alignment lines.
   - Front/Back camera switch and flash toggle.
6. **Policy-Compliant, Non-Intrusive Monetization**
   - Google AdMob Interstitial ad prior to photo download with frequency capping.
   - **Fail-Safe Fallback:** If an ad fails to load or the device is offline, **download is NEVER blocked**. Usability is never sacrificed.
7. **Modern Android Scoped Storage**
   - Saves to `Pictures/FormPic` using MediaStore APIs (no unnecessary legacy permissions required on Android 10+).

---

## 🏗️ Architecture & Technology Stack

```
com.formpic.app/
├── data/
│   ├── model/
│   │   ├── PhotoPreset.kt             # Official presets specification model
│   │   ├── ProcessingState.kt         # MVI/StateFlow pipeline state
│   │   └── QualityCheckReport.kt      # Face centering & tilt diagnostic report
│   └── repository/
│       └── PresetRepository.kt        # Official Indian portal specifications
├── engine/
│   ├── BitmapUtils.kt                 # Memory-safe decoding & EXIF rotation
│   ├── FaceDetectorEngine.kt          # ML Kit Face Detection & passport framing
│   ├── BackgroundRemoverEngine.kt     # ML Kit Selfie Segmentation & white blending
│   ├── ExactKbCompressor.kt           # Binary search quality compressor
│   └── StorageManager.kt              # MediaStore scoped storage & sharing
├── ads/
│   └── AdManager.kt                   # AdMob Interstitial wrapper with safe fallback
├── ui/
│   ├── theme/                         # Government-trust Navy, Emerald, Blue palette
│   ├── components/                    # PassportGuideOverlay, BeforeAfterSlider, etc.
│   ├── screens/                       # Home, Camera, Presets, Processing, Result, etc.
│   └── viewmodel/                     # Central PhotoProcessViewModel
└── FormPicApp.kt & MainActivity.kt    # Compose Navigation entry points
```

---

## 🧪 Testing

Comprehensive unit tests verify:
- `ExactKbCompressorTest.kt`: Validates that Under 25 KB, Under 50 KB, Under 75 KB, Under 100 KB, and Custom 30 KB always produce output strictly `<= Target Bytes`.
- `PresetRepositoryTest.kt`: Validates published Indian government dimensions, aspect ratios, and official source links.
- `FaceFramingTest.kt`: Validates face centering and passport bounding box calculations.

---

## 🚀 Building and Running

### Prerequisites
- Android Studio Ladybug / Iguana or later
- JDK 17
- Android SDK 34

### Open in Android Studio
1. Launch Android Studio.
2. Select **Open** and choose the `c:\Users\ritik\Downloads\pic_app` directory.
3. Allow Gradle to sync dependencies.
4. Connect an Android device (or launch an emulator) and click **Run (Shift + F10)**.

### Build from Command Line
```bash
# Debug APK
.\gradlew.bat assembleDebug

# Run Unit Tests
.\gradlew.bat test

# Release App Bundle (for Play Store)
.\gradlew.bat bundleRelease
```

---

## 📄 License & Publishing
- Documentation and store listing: see [`docs/PLAY_STORE_PUBLISHING.md`](docs/PLAY_STORE_PUBLISHING.md)
- Privacy Policy: see [`docs/PRIVACY_POLICY.md`](docs/PRIVACY_POLICY.md)
- Data Safety Guide: see [`docs/DATA_SAFETY.md`](docs/DATA_SAFETY.md)
- Release & Signing: see [`docs/RELEASE_GUIDE.md`](docs/RELEASE_GUIDE.md)
