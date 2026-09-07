# 📊 ValidPic In-App Feedback Hub (Connected to Google Forms & Sheets)

> [!NOTE]
> **Live Form Endpoint Configured in v1.0.8**:
> - Form Response URL: `https://docs.google.com/forms/d/e/1FAIpQLSdIuQexxVY2USXe7bNNOXYUxCtDQe0nXmZV30qY6hmn5anoWA/formResponse`
> - Verified Status: **HTTP 200 OK** (Live on-device submissions route directly to your Google Form)
> - To view responses in Google Sheets: Open your Google Form -> click **Responses** -> click **Link to Sheets**.
> - To receive instant emails: In Google Sheets -> click **Tools** -> **Notification settings** -> **Edit notifications** -> select *“A user submits a form”* + *“Email right away”*.

ValidPic features silent, in-app feedback submission. Candidates can request new exam presets (e.g. State PSCs, Police, SSC, IBPS, Universities) or report portal issues with a single tap—**without ever leaving the app or opening Gmail**.

---

## ⚡ Option 1: Using a Free Google Form (Recommended — 2 Minutes)

1. Go to [forms.google.com](https://forms.google.com) and create a new blank form named **"ValidPic User Feedback"**.
2. Add 4 Short/Paragraph questions:
   - **Topic** (Short answer)
   - **Message** (Paragraph)
   - **Contact Email** (Short answer)
   - **Device Diagnostics** (Short answer)
3. Click the 3 dots in the top right -> **Get pre-filled link**.
4. Type sample answers in the 4 boxes: `topic`, `message`, `email`, `device` -> click **Get link** -> **Copy link**.
5. The copied link looks like:
   ```
   https://docs.google.com/forms/d/e/1FAIpQLScXXXXXXXX/viewform?usp=pp_url&entry.123456=topic&entry.234567=message&entry.345678=email&entry.456789=device
   ```
6. Open [`FeedbackManager.kt`](file:///c:/Users/ritik/Downloads/pic_app/app/src/main/java/com/validpic/app/data/repository/FeedbackManager.kt) and paste:
   ```kotlin
   submissionUrl = "https://docs.google.com/forms/d/e/1FAIpQLScXXXXXXXX/formResponse"
   entryCategory = "entry.123456"
   entryMessage = "entry.234567"
   entryEmail = "entry.345678"
   entryDevice = "entry.456789"
   ```
7. In your Google Form, click the **Responses** tab -> **Link to Sheets** (Create a new spreadsheet).
8. Every time a candidate submits feedback in ValidPic, a new row appears instantly in your private Google Sheet!
9. *(Optional)* In Google Sheets, click **Tools** -> **Notification rules** -> *"A user submits a form: Email right away"*. Google will automatically email you on every submission!

---

## ⚡ Option 2: Direct Google Apps Script Webhook (Zero Forms)

1. Open a new Google Sheet at [sheets.new](https://sheets.new).
2. Click **Extensions** -> **Apps Script**.
3. Replace the code with this 8-line script:
   ```javascript
   function doPost(e) {
     var sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
     var data = JSON.parse(e.postData.contents);
     sheet.appendRow([new Date(), data.category, data.message, data.email, data.device]);
     return ContentService.createTextOutput("SUCCESS").setMimeType(ContentService.MimeType.TEXT);
   }
   ```
4. Click **Deploy** -> **New deployment** -> Select type: **Web app**.
   - Execute as: **Me**
   - Who has access: **Anyone**
5. Click **Deploy** and copy the **Web app URL** (`https://script.google.com/macros/s/.../exec`).
6. Paste into [`FeedbackManager.kt`](file:///c:/Users/ritik/Downloads/pic_app/app/src/main/java/com/validpic/app/data/repository/FeedbackManager.kt):
   ```kotlin
   submissionUrl = "https://script.google.com/macros/s/YOUR_DEPLOYMENT_ID/exec"
   ```
That's it! Instant, direct row creation in your spreadsheet.
