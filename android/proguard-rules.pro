# LibGDX ProGuard Rules
-keep class com.badlogic.gdx.** { *; }
-keep class com.badlogic.gdx.backends.android.** { *; }
-keep class com.badlogic.gdx.graphics.** { *; }
-keep class com.badlogic.gdx.graphics.g3d.** { *; }
-keep class com.badlogic.gdx.graphics.g3d.utils.** { *; }
-keep class com.badlogic.gdx.math.** { *; }
-keep class com.badlogic.gdx.physics.box2d.** { *; }
-keep class com.badlogic.gdx.utils.** { *; }
-keep class com.badlogic.gdx.assets.** { *; }

# Ashley ECS
-keep class com.badlogic.ashley.** { *; }

# Freetype
-keep class com.badlogic.gdx.graphics.g2d.freetype.** { *; }

# Keep native method names
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enum values
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}