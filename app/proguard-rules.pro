# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================
# OBFUSCATION SETTINGS
# ============================================

# Keep line numbers for stack traces
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Obfuscate package names
-repackageclasses ''
-allowaccessmodification

# ============================================
# HILT / DAGGER
# ============================================

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-keepclasseswithmembernames class * {
    @dagger.hilt.* <fields>;
}
-keepclasseswithmembernames class * {
    @dagger.hilt.* <methods>;
}
-keepclasseswithmembernames class * {
    @javax.inject.* <fields>;
}

# ============================================
# RETROFIT / OKHTTP
# ============================================

# Keep Retrofit interfaces
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Retrofit does reflection on generic parameters
-keepattributes Signature

# Keep generic type info for Retrofit
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ============================================
# GSON / JSON SERIALIZATION
# ============================================

# Keep Gson annotations
-keepattributes *Annotation*

# Keep classes with @SerializedName
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes (DTOs) for Gson
-keep class com.example.eloanmust.feature.auth.data.dto.** { *; }
-keep class com.example.eloanmust.feature.loan.data.dto.** { *; }
-keep class com.example.eloanmust.feature.notification.data.dto.** { *; }
-keep class com.example.eloanmust.feature.product.data.dto.** { *; }
-keep class com.example.eloanmust.feature.profile.data.dto.** { *; }
-keep class com.example.eloanmust.core.network.ApiResponse { *; }
-keep class com.example.eloanmust.core.network.PaginatedResponse { *; }
-keep class com.example.eloanmust.core.network.ErrorResponse { *; }

# ============================================
# ROOM DATABASE
# ============================================

-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# ============================================
# FIREBASE
# ============================================

-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ============================================
# COMPOSE
# ============================================

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============================================
# COROUTINES
# ============================================

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================
# KOTLIN
# ============================================

-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ============================================
# SECURITY - Root Detection
# ============================================

# Keep root detection helper
-keep class com.example.eloanmust.core.security.** { *; }

# ============================================
# GENERAL
# ============================================

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelables
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep Serializables
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Remove Timber logging in release
-assumenosideeffects class timber.log.Timber {
    public static void v(...);
    public static void d(...);
    public static void i(...);
}