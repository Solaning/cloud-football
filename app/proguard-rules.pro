# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

 -optimizationpasses 5
 -ignorewarnings
 -dontwarn
 -dontskipnonpubliclibraryclassmembers
 -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
native <methods>;
}
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
	public void *(android.view.View);
}
-keepclassmembers enum * {
	public static **[] values();
	public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
	public static final android.os.Parcelable$Creator *;
	private <fields>;
}
-keep public class * implements java.io.Serializable{
	public protected private *;
}
-keep class **.R$* { *; }

#一些gson的实体类
-keep class com.kinth.football.bean.** {*;}
-keep class com.kinth.football.chat.bean.** {*;}
-keep class com.kinth.football.ui.cloud5ranking.** {*;}
-keep class com.kinth.football.ui.team.formation.** {*;}
-keep public class android.app.IActivityWatcher { *; }

# gson的混淆
#-libraryjars libs/gson-2.3.1.jar  eclipse
-keep class com.google.gson.** { *;}
-keep class org.json.** { *; }
-keep class * extends com.google.gson.stream.** { *; }
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

-keepclassmembers class * extends android.support.v4.app.Fragment {
    public void * (android.view.View);
    public boolean * (android.view.View);
}

##---------------End: proguard configuration for Gson  ----------


# nineoldandroids动画lib包
#-libraryjars libs/nineoldandroids-2.4.0.jar
-dontwarn com.nineoldandroids.*
-keep class com.nineoldandroids.** { *;}

# xUtils-2.6.14.jar
#-libraryjars libs/xUtils-2.6.14.jar
-keep class * extends java.lang.annotation.Annotation { *; }
-keep class com.kinth.mmspeed.bean.** { *; }

# commons-lang3
#-libraryjars libs/commons-lang3-3.4.jar

# BaiduLBS-Android
#-libraryjars libs/baidumapapi_v3_0_0.jar
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

# pinyin4j
#-libraryjars libs/pinyin4j-2.5.0.jar
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
-keep class net.sourceforge.pinyin4j.** { *; }
-keep class demo.** { *;}

#项目中用到了webview的复杂操作
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}

#---smack---
-keep class org.jivesoftware.smack.** { *; }
-keep class org.jivesoftware.smackx.** { *; }

-keep class org.projectmaxs.shared.global.util.Log
-keep class org.projectmaxs.shared.maintransport.TransportInformation

################################################################################
# Custom proguard settings

# We don't use jzlib, but instead the Android API for compression
-dontnote com.jcraft.jzlib.*

-dontnote android.os.SystemProperties

-dontnote sun.security.pkcs11.SunPKCS11

# See http://stackoverflow.com/questions/5701126
-optimizations !code/allocation/variable

# Smack specific configuration
-keep class org.jivesoftware.smack.debugger.JulDebugger { *; }
-keep class * implements org.jivesoftware.smack.initializer.SmackInitializer
-keep class * implements org.jivesoftware.smack.provider.IQProvider
-keep class * implements org.jivesoftware.smack.provider.ExtensionElementProvider
-keep class * extends org.jivesoftware.smack.packet.Stanza
-keep class org.jivesoftware.smack.XMPPConnection
-keep class org.jivesoftware.smack.util.dns.minidns.MiniDnsResolver
-keep class org.jivesoftware.smackx.disco.ServiceDiscoveryManager
-keep class org.jivesoftware.smackx.xhtmlim.XHTMLManager
-keep class org.jivesoftware.smackx.muc.MultiUserChat
-keep class org.jivesoftware.smackx.bytestreams.ibb.InBandBytestreamManager
-keep class org.jivesoftware.smackx.bytestreams.socks5.Socks5BytestreamManager
-keep class org.jivesoftware.smackx.filetransfer.FileTransferManager
-keep class org.jivesoftware.smackx.iqlast.LastActivityManager
-keep class org.jivesoftware.smackx.commands.AdHocCommandManager
-keep class org.jivesoftware.smackx.ping.PingManager
-keep class org.jivesoftware.smackx.privacy.PrivacyListManager
-keep class org.jivesoftware.smackx.time.EntityTimeManager
-keep class org.jivesoftware.smackx.vcardtemp.VCardManager
-keep class org.jivesoftware.smackx.receipts.DeliveryReceiptManager

-keepclasseswithmembers class * extends org.jivesoftware.smack.sasl.SASLMechanism {
    public <init>(org.jivesoftware.smack.SASLAuthentication);
}

#--greenDao---
#-libraryjars libs/greendao-1.3.7.jar
-keep class de.greenrobot.dao.** {*;}
#保持greenDao的方法不被混淆 #用来保持生成的表名不被混淆
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

-keep class cn.smssdk.SMSSDKUIShell
-keep class cn.smssdk.** {*;}

#移除日志代码
-assumenosideeffects class android.util.Log {
	public static *** d(...);
	public static *** e(...);
	public static *** i(...);
	public static *** w(...);
}

#---eventBus ---
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keepclassmembers class ** {
    public void getName(**);
}
#---------------