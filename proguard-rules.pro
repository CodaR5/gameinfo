-ignorewarnings
-dontoptimize
-dontshrink
-dontwarn
-dontnote

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes StackMapTable
-keepattributes LineNumberTable
-keepattributes SourceFile

-keep class com.codar5.gameinfo.Gameinfo { *; }
-keep class com.codar5.gameinfo.GameinfoClient { *; }
-keep class com.codar5.gameinfo.gui.ModMenuIntegration { *; }

-keep class * implements net.fabricmc.api.ModInitializer { *; }
-keep class * implements net.fabricmc.api.ClientModInitializer { *; }

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepnames class * implements net.minecraft.network.protocol.common.custom.CustomPacketPayload

-repackageclasses ''
-allowaccessmodification
