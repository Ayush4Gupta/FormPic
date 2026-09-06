# Privacy Policy for FormPic

**Last Updated:** September 2026

FormPic ("we", "our", or "the app") is committed to protecting your personal privacy. This Privacy Policy explains our privacy practices regarding information collected, processed, and maintained when you use the FormPic mobile application.

---

### 1. 100% On-Device Image Processing

FormPic is designed with an offline-first, privacy-by-design architecture:
- **Zero Photo Uploads:** All photo captures, gallery image selections, face detection algorithms, background segmentation, and file compression operations occur **entirely locally on your device**.
- **No Remote Servers for Media:** We do not operate remote servers or cloud storage for user images. Your photos, biometric likeness, and facial features **never leave your physical smartphone**.
- **On-Device Artificial Intelligence:** We utilize Google ML Kit on-device libraries. Image data is evaluated in temporary memory and is not stored or shared by Google ML Kit.

---

### 2. Device Storage & Local Files

- **Saved Photos:** When you tap "Download Photo", your processed image is written into your smartphone's standard media gallery under `Pictures/FormPic` using Android MediaStore APIs.
- **Temporary Cache:** Any temporary image files generated during processing are saved in the app's internal sandbox cache and are deleted upon completing the session or when you select "Clear Cache" in the Settings screen.

---

### 3. Advertising (Google AdMob)

FormPic is provided free of charge and supported by advertising. We integrate the Google Mobile Ads SDK (Google AdMob) to display advertisements:
- Google AdMob may collect and process information such as your mobile Advertising ID (AAID), device IP address, non-sensitive hardware characteristics, and diagnostic crash logs.
- This data is used by Google to deliver and measure advertisements in accordance with the [Google Privacy Policy](https://policies.google.com/privacy) and [Google Play Developer Program Policies](https://play.google.com/about/developer-content-policy/).
- You can reset or opt out of personalized advertising at any time through your Android device settings under *Settings > Google > Ads*.

---

### 4. Permissions Requested

FormPic requests only the minimum permissions essential for core functionality:
- **Camera (`android.permission.CAMERA`):** Required only when you choose to capture a new photo within the app. You can choose to use the gallery picker instead.
- **Media Access (`READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE`):** Required only to let you select a photo from your gallery.
- **Internet (`android.permission.INTERNET`):** Used solely to load advertisements from Google AdMob and to open external web links (e.g., this privacy policy or Play Store rating).

---

### 5. Children's Privacy

FormPic does not knowingly collect any personal identifiable information from children under the age of 13.

---

### 6. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Any changes will be reflected with an updated "Last Updated" date at the top of this page.

---

### 7. Contact Us

If you have questions or feedback regarding this Privacy Policy, please contact us at:
- **Email:** `support@formpic.app`
- **Developer Support:** Via the Google Play Store developer contact link.
