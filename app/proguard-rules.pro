# Kotlin
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**

# Kotlinx Serialization
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclasseswithmembers class ** { @kotlinx.serialization.Serializable *; }

# Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class io.netty.** { *; }
-dontwarn io.netty.**

# Coil
-keep class coil.** { *; }
-dontwarn coil.**

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# WorkManager + Hilt Worker
-keep class * extends androidx.work.ListenableWorker { public <init>(...); }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.InstallIn class *

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Hilt / Dagger
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.**

# Eigene Datenmodelle (kein Obfuscating)
-keep class com.instadownloader.data.model.** { *; }
-keep class com.instadownloader.data.local.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }