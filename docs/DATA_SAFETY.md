# ValidPic — Google Play Console Data Safety Form Guide

Use this reference to complete the **Data Safety** section in Google Play Console.

---

### 1. Data Collection and Sharing Overview

| Question | Answer | Rationale |
|---|---|---|
| **Does your app collect or share any of the required user data types?** | **Yes** | Due to third-party SDK (Google AdMob) collecting device identifiers for ad serving. |
| **Is all of the user data collected by your app encrypted in transit?** | **Yes** | AdMob transmits network payloads over HTTPS / TLS. |
| **Do you provide a way for users to request that their data be deleted?** | **Yes** | Users can reset advertising IDs or clear local app cache at any time. |

---

### 2. Specific Data Types Declaration

#### A. Photos and Videos
- **Is this data collected?** **NO**.
  - *Explanation:* Photos and videos are processed purely on-device via local ML Kit models. No user photos or facial biometric data are uploaded or collected.
- **Is this data shared?** **NO**.

#### B. Location
- **Is this data collected?** **NO** (Precise or coarse location is NOT requested or accessed).

#### C. Device or Other Identifiers (Device ID, Advertising ID)
- **Data Type:** Device or other IDs (Advertising ID).
- **Collected?** **Yes** (Collected by Google AdMob SDK).
- **Shared?** **Yes** (Shared with Google AdMob).
- **Purposes:**
  - Advertising or marketing.
  - Analytics / Fraud prevention.
- **Is this data encrypted in transit?** **Yes**.
- **Is collection optional?** **Yes** (Users can reset/opt out of ad personalization in device settings).

#### D. App Performance & Diagnostics
- **Crash logs / Diagnostics:** Handled by Google Play Services / AdMob for crash monitoring.

---

### 3. Target Audience & Content Rating
- **Target Age:** 13 and older (General audience applying for exams, colleges, and government documents).
- **App Category:** Tools / Photography.
