# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Firebase model class protect
-keep class pnpmsjm.com.ourmasjid.Members { *; }

# Firebase database deserialization
-keepattributes Signature
-keepattributes *Annotation*

# Prevent Firebase reflection-related stripping
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Glide support
-keep class com.bumptech.glide.** { *; }
-keep interface com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Prevent stripping model fields
-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <methods>;
}
