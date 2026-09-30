-dontobfuscate

# Preserve generic signatures, annotations, and enclosing methods for Gson
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses

# Preserve data models deserialized by Gson
-keep class com.example.planlekcji.ckziu_elektryk.client.** { *; }
-keep class com.example.planlekcji.replacements.DayReplacements { *; }

# Preserve Gson's TypeToken subclasses (needed for List<...> deserialization)
-keep class * extends com.google.gson.reflect.TypeToken

# Strip android.util.Log invocations in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}