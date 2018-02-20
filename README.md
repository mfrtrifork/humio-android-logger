# humio-android-logger

In your ``build.gradle`` file:

```groovy
dependencies {
    implementation 'com.github.mfrtrifork.humio-android-logger:library:NEWEST_VERSION'
}
```

Check newest version of this plugin here: http://gradleplease.appspot.com/#humio-android-logger

```groovy
# Retrofit
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.* { *; }
-dontwarn javax.annotation.**
# Humio logger models
-keep class io.humio.androidlogger.models.** { *; }
```