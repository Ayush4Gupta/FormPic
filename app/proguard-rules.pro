# ValidPic R8 / ProGuard Configuration

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ML Kit Face Detection & Selfie Segmentation
-keep class com.google.mlkit.vision.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_** { *; }
-dontwarn com.google.mlkit.vision.**

# CameraX
-keep class androidx.camera.core.** { *; }
-dontwarn androidx.camera.core.**

# Google Mobile Ads SDK (AdMob)
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep public class com.google.ads.** {
   public *;
}
-dontwarn com.google.android.gms.ads.**

# Keep models for JSON / State serialization if needed
-keepclassmembers class com.validpic.app.data.model.** {
    <fields>;
    <methods>;
}
