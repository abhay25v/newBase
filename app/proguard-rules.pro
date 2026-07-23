# Add project specific ProGuard rules here.

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.landmarkgroup.sahlawarehouse.**$$serializer { *; }
-keepclassmembers class com.landmarkgroup.sahlawarehouse.** {
    *** Companion;
}
-keepclasseswithmembers class com.landmarkgroup.sahlawarehouse.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit / OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp

# Keep data/model classes used for serialization
-keep class com.landmarkgroup.sahlawarehouse.data.** { *; }
-keep class com.landmarkgroup.sahlawarehouse.domain.model.** { *; }
