-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}

-keepclassmembers class **.R$* {
       public static <fields>;
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}
-keep public class com.google.android.gms.ads.** {
    public *;
}

-keep public class com.google.ads.** {
    public *;
}

-dontwarn com.google.android.gms.**

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn com.atech.theme.R$layout
-dontwarn com.atech.theme.R$string