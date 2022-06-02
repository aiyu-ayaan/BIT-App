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