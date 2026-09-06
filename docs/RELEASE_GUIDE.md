# FormPic — Release & Signing Guide

Follow these steps to produce an optimized, signed Android App Bundle (`.aab`) ready for upload to Google Play Console.

---

## 1. Keystore Generation

Run the following command in your terminal to generate your production release keystore:

```bash
keytool -genkeypair -v \
  -keystore formpic-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias formpic-key
```

*Keep `formpic-release.jks` and your keystore passwords in a safe password manager. Never commit them to Git.*

---

## 2. Release Signing Configuration

Create a `keystore.properties` file in your root project directory (this file is git-ignored):

```properties
storeFile=../formpic-release.jks
storePassword=YOUR_KEYSTORE_PASSWORD
keyAlias=formpic-key
keyPassword=YOUR_KEY_PASSWORD
```

In `app/build.gradle.kts`, load these properties into your `signingConfigs`:

```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

signingConfigs {
    create("release") {
        storeFile = file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
    }
}
```

---

## 3. Production AdMob App ID & Ad Units

Before publishing, replace the test AdMob IDs in:
1. `app/build.gradle.kts`:
   ```kotlin
   manifestPlaceholders["admobAppId"] = "ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
   ```
2. `app/src/main/java/com/formpic/app/ads/AdManager.kt`:
   ```kotlin
   private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
   ```

---

## 4. Building the Production Android App Bundle (AAB)

To generate the final optimized release bundle with R8 minification and resource shrinking enabled:

```bash
# Windows
.\gradlew.bat bundleRelease

# macOS / Linux
./gradlew bundleRelease
```

The output bundle will be located at:
`app/build/outputs/bundle/release/app-release.aab`

---

## 5. Google Play Console Upload & Rollout Steps

1. Log in to [Google Play Console](https://play.google.com/console).
2. Select or create the app: **FormPic: Passport & Exam Photo**.
3. Complete the **Set up your app** tasks:
   - Privacy policy link: URL pointing to your hosted `PRIVACY_POLICY.md`.
   - App access: All functionality is available without special access.
   - Ads: Select "Yes, my app contains ads".
   - Content rating: Complete the questionnaire (Rating: Everyone / 3+).
   - Target audience: 13+.
   - Data safety: Complete according to `docs/DATA_SAFETY.md`.
   - Government apps: Select "No" (FormPic is a third-party helper utility, not an official government app).
4. Go to **Release > Testing > Internal testing** or **Closed testing**.
5. Create a new release and upload `app-release.aab`.
6. Enter release notes:
   ```text
   Initial production release of FormPic.
   • 1-tap exact KB compression (Under 25 KB, Under 50 KB, Under 75 KB, Under 100 KB)
   • Automatic on-device clean white background isolation
   • Pre-configured presets for Indian Passport, SSC, UPSC, IBPS, NEET & PAN
   • 100% on-device private processing
   ```
7. Review and rollout!
