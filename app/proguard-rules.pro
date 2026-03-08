# Add project specific ProGuard rules here.

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Moshi
-keep class com.bikeshare.app.data.api.dto.** { *; }
-keepclassmembers class com.bikeshare.app.data.api.dto.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep data classes for serialization
-keep class com.bikeshare.app.domain.model.** { *; }

# osmdroid
-dontwarn org.osmdroid.**
