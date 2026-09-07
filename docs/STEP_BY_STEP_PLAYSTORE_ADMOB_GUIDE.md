# Complete Step-by-Step Guide: Google Play Publishing & AdMob Integration for ValidPic

This guide walks you through every exact step to set up your Google AdMob advertising, prepare your Google Play Console account, build your production `.aab`, pass Google's latest 12-tester review, and launch **ValidPic** to the public.

---

## Table of Contents
1. [Phase 1: Google AdMob Production Setup](#phase-1-google-admob-production-setup)
2. [Phase 2: Connecting Real AdMob IDs in ValidPic Code](#phase-2-connecting-real-admob-ids-in-validpic-code)
3. [Phase 3: Setting Up Free `app-ads.txt` (Crucial for Ad Revenue)](#phase-3-setting-up-free-app-adstxt-crucial-for-ad-revenue)
4. [Phase 4: Hosting Your Privacy Policy for Free (2 Minutes)](#phase-4-hosting-your-privacy-policy-for-free-2-minutes)
5. [Phase 5: Google Play Console Account Setup](#phase-5-google-play-console-account-setup)
6. [Phase 6: Generating Production Keystore & Building the `.aab`](#phase-6-generating-production-keystore--building-the-aab)
7. [Phase 7: Google Play Console App Setup & Form Declarations](#phase-7-google-play-console-app-setup--form-declarations)
8. [Phase 8: Closed Testing Requirement (12 Testers for 14 Days) & Production Launch](#phase-8-closed-testing-requirement-12-testers-for-14-days--production-launch)

---

## Phase 1: Google AdMob Production Setup

### 1. Create Your AdMob Account
1. Go to [https://admob.google.com](https://admob.google.com).
2. Sign in with your primary Google Account (preferably the same Google account you use for Google Play Console).
3. Complete account registration and add your payment address / bank details.

### 2. Add ValidPic to AdMob
1. In the AdMob sidebar, click **Apps** ➔ **Add App**.
2. Platform: **Android**.
3. Is the app listed on a supported app store?: Select **No** (since we haven't published it yet).
4. App name: **ValidPic**.
5. Click **Add App**.
6. AdMob will generate your unique **AdMob App ID** (format: `ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX`). **Copy and save this ID**.

### 3. Create the Interstitial Ad Unit
1. Inside your new ValidPic app dashboard in AdMob, click **Ad units** ➔ **Add ad unit**.
2. Select **Interstitial**.
3. Ad unit name: `Download_Interstitial`.
4. Advanced settings:
   - Frequency capping: Enable frequency cap: e.g., *1 impression per 1 minute per user* (prevents showing back-to-back ads if users download multiple times).
5. Click **Create ad unit**.
6. AdMob will give you your **Interstitial Ad Unit ID** (format: `ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY`). **Copy and save this ID**.

---

## Phase 2: Connecting Real AdMob IDs in ValidPic Code

Currently, ValidPic is configured with Google's official sample test IDs so the app never crashes during development. When you are ready for release, update these two files:

### 1. In `app/build.gradle.kts`
Change line 23:
```kotlin
// Replace with your real AdMob App ID from Phase 1, Step 2:
manifestPlaceholders["admobAppId"] = "ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
```

### 2. In `app/src/main/java/com/validpic/app/ads/AdManager.kt`
Update the Ad Unit ID on line 19:
```kotlin
// Change from test ID to your real Interstitial Ad Unit ID:
private const val PRODUCTION_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY"
```

### 3. Register Your Test Phone (Critical to Prevent Account Ban!)
> [!CAUTION]
> **NEVER tap live ads on your own phone!** Google's automated systems detect self-clicks as "Invalid Traffic" and will suspend or terminate your AdMob account.
> 
> To test safely on your personal physical phone, get your device's AdMob Test Device ID from Logcat when running the app (search for `use RequestConfiguration.Builder().setTestDeviceIds`), or in AdMob:
> **Settings ➔ Test Devices ➔ Add Test Device**.

---

## Phase 3: Setting Up Free `app-ads.txt` (Crucial for Ad Revenue)

Google AdMob requires an `app-ads.txt` file hosted on your developer website. If you don't have this, AdMob will restrict ad serving and earnings.

### How to host it completely free on GitHub Pages:
1. In AdMob, go to **Apps** ➔ **app-ads.txt** and copy the code snippet provided (looks like: `google.com, pub-XXXXXXXXXXXXXXXX, DIRECT, f08c47fec0942fa0`).
2. Create a free public GitHub repository named `yourusername.github.io` (or any repository with GitHub Pages enabled).
3. Add a file named `app-ads.txt` at the root of the site containing that snippet.
4. Enable GitHub Pages in the repo settings.
5. Your file will be live at `https://yourusername.github.io/app-ads.txt`.
6. In Google Play Console under **Store settings ➔ Website**, enter `https://yourusername.github.io`. AdMob will automatically verify it within 24 hours!

---

## Phase 4: Hosting Your Privacy Policy for Free (2 Minutes)

Google Play **strictly rejects** apps without a live, publicly accessible HTTPS Privacy Policy URL.

We have already created the complete, policy-compliant text in [`docs/PRIVACY_POLICY.md`](file:///c:/Users/ritik/Downloads/pic_app/docs/PRIVACY_POLICY.md).

### Quick Free Hosting Options:
- **Option A (GitHub Pages):** Place `PRIVACY_POLICY.md` in your GitHub repository and enable GitHub Pages or GitHub README link: `https://yourusername.github.io/validpic/privacy-policy`.
- **Option B (Google Sites):** Create a free one-page site at [sites.google.com](https://sites.google.com), paste the text from `docs/PRIVACY_POLICY.md`, and click **Publish**.
- **Option C (Notion / Vercel):** Create a public Notion page with the privacy policy and copy the web link.

---

## Phase 5: Google Play Console Account Setup

1. Go to [https://play.google.com/console](https://play.google.com/console).
2. Pay the one-time developer registration fee of **$25 USD**.
3. Choose your account type:
   - **Personal Account:** Requires verifying your government photo ID and phone number.
   - **Organization Account:** Requires an official business registration and a D-U-N-S number.
4. Complete Google's identity verification process (usually approved in 24–48 hours).

---

## Phase 6: Generating Production Keystore & Building the `.aab`

Google Play requires all new apps to be submitted as an **Android App Bundle (`.aab`)** signed with an upload key.

### 1. Generate Your Release Keystore
Run this command in PowerShell inside `c:\Users\ritik\Downloads\pic_app`:

```powershell
keytool -genkeypair -v -keystore validpic-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias validpic-key
```
You will be prompted to enter a password and your name/organization. Remember this password!

### 2. Create `keystore.properties`
Create a file named `keystore.properties` in your root folder:
```properties
storeFile=../validpic-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=validpic-key
keyPassword=YOUR_KEY_PASSWORD
```

### 3. Build the Release Bundle
Run:
```powershell
.\gradlew.bat bundleRelease
```
Your release file will be created at:
`c:\Users\ritik\Downloads\pic_app\app\build\outputs\bundle\release\app-release.aab`

---

## Phase 7: Google Play Console App Setup & Form Declarations

In Google Play Console, click **Create app**:
- **App name:** `ValidPic: Passport & Exam Photo`
- **Default language:** English (United States) or English (India)
- **App or game:** App
- **Free or paid:** Free
- Accept developer declarations and click **Create app**.

### Fill Out "App Content" Declarations:

1. **Privacy Policy:** Paste your public URL from Phase 4.
2. **App Access:** Select *"All functionality is available without special access"*.
3. **Ads:** Select *"Yes, my app contains ads"*.
4. **Content Rating:**
   - Complete the IARC questionnaire.
   - Category: Utility / Tools.
   - Violence, offensive language, sexual content: Select "No" to all.
   - You will receive an **Everyone (3+) / PEGI 3** rating.
5. **Target Audience & Content:**
   - Target age: **13 and older** (13–15, 16–17, 18+).
   - Could this app appeal to children? Select **No**.
6. **Government Apps:**
   - Select: **"No, this app is not developed by or on behalf of a government entity"**.
7. **Financial Features:**
   - Select: *"My app doesn't provide any financial features"*.
8. **Data Safety Form (Crucial):**
   - Follow the exact answers prepared in [`docs/DATA_SAFETY.md`](file:///c:/Users/ritik/Downloads/pic_app/docs/DATA_SAFETY.md):
     - Photos/Videos: **NOT collected**, **NOT shared** (100% on-device).
     - Device or Other IDs: **Collected & Shared** (used by Google AdMob for ads and fraud prevention).
     - Encrypted in transit: **Yes**.
     - Data deletion available: **Yes**.
9. **Advertising ID Declaration:**
   - Does your app use an Advertising ID? Select **Yes**.
   - Check the box for: **Advertising or marketing**.

### Upload Store Listing Assets:
- **App Icon:** Upload `docs/assets/playstore_icon_512.jpg` (512 × 512 px).
- **Feature Graphic:** Upload `docs/assets/playstore_feature_graphic.jpg` (1024 × 500 px).
- **Screenshots:** Upload at least 4 phone screenshots (minimum 1080 × 1920 px).
- **Descriptions:** Copy the formatted title, short description, and full description from [`docs/PLAY_STORE_PUBLISHING.md`](file:///c:/Users/ritik/Downloads/pic_app/docs/PLAY_STORE_PUBLISHING.md).

---

## Phase 8: Closed Testing Requirement (12 Testers for 14 Days) & Production Launch

> [!IMPORTANT]
> **Google's Updated Testing Policy:**
> For personal developer accounts, Google requires you to run a **Closed Test** with at least **12 testers** who remain opted-in for **14 continuous days** before you can unlock Production release access.

### How to complete this smoothly:
1. Go to **Testing ➔ Closed testing**.
2. Click **Create track** (or use the default Closed testing track).
3. Create a **Testers Email List** (create an email list and add 12 to 15 Gmail addresses of friends, family, or colleagues).
4. Create a new release, upload `app-release.aab`, and rollout the track.
5. Copy the **Join on Android** or **Join on Web** opt-in URL.
6. Send the opt-in link to your 12 testers. Each tester must:
   - Click the link and accept the invite.
   - Install the app on their phone.
   - Open and use the app occasionally over the 14-day period.
7. After 14 days have passed and your dashboard shows 14 days of continuous testing with active engagement:
   - Click **Apply for production**.
   - Answer Google's simple questions about how you gathered feedback and fixed bugs.
   - Google usually approves production access within 2 to 5 business days!
8. Go to **Production ➔ Create release ➔ Promote from Closed testing** ➔ **Rollout to Production**!

---

## Summary Checklist

| Step | Action | Status |
|---|---|---|
| 1 | Create AdMob account & get Interstitial Ad Unit ID | Ready to execute |
| 2 | Replace test AdMob IDs in `build.gradle.kts` & `AdManager.kt` | Ready |
| 3 | Host `app-ads.txt` and `PRIVACY_POLICY.md` | Prepared in `docs/` |
| 4 | Generate Keystore & build `app-release.aab` | Script ready |
| 5 | Upload assets (`docs/assets/playstore_icon_512.jpg` & `feature_graphic.jpg`) | Generated & Ready |
| 6 | Complete Play Console declarations (Data Safety, Ads, Rating) | Guide ready in `docs/DATA_SAFETY.md` |
| 7 | Run 12-tester closed test for 14 days and launch! | Clear instructions provided |
