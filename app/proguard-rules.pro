# SocialKids - reglas ProGuard
-keepattributes *Annotation*
-keep class com.socialkids.app.data.local.entity.** { *; }
-dontwarn kotlinx.coroutines.**
