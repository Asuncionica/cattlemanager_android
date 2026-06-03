# Retrofit + OkHttp
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

# Gson
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Modelos de datos (evitar que Gson falle al deserializar)
-keep class com.example.cattlemanager.model.** { *; }
-keep class com.example.cattlemanager.activities.LoginRequest { *; }
-keep class com.example.cattlemanager.activities.RolResponse { *; }
-keep class com.example.cattlemanager.activities.UsuarioResponse { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Conservar información de línea para stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
