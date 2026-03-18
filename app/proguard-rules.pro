# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/likun/.local/share/fnm/node-versions/v25.8.1/installation/lib/node_modules/@google/gemini-cli/node_modules/@google/gemini-cli-core/dist/src/sdk/tools/proguard/proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any custom keep rules here that are specific to your project

# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase
-keep class com.github.damontecres.stashapp.data.room.** { *; }

# Keep GraphQL generated code
-keep class com.github.damontecres.stashapp.api.** { *; }

# Keep models and entities
-keep class com.github.damontecres.stashapp.data.** { *; }

# Keep Acra (for crash reporting if used)
-keep class org.acra.** { *; }

# Keep Compose UI
-keep class androidx.compose.** { *; }
