# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers @kotlinx.serialization.Serializable class io.github.garoluis.anotherlifecounter.** {
    *** Companion;
}
-keepclasseswithmembers class io.github.garoluis.anotherlifecounter.**$$serializer {
    *** INSTANCE;
}
-keepclassmembers class io.github.garoluis.anotherlifecounter.** {
    *** write$Self(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
