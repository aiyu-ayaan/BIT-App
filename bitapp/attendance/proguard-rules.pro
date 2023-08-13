-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}

-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn com.atech.theme.R$color
-dontwarn com.atech.theme.R$drawable
-dontwarn com.atech.theme.R$string
-dontwarn com.google.android.material.R$attr
-dontwarn com.atech.theme.R$layout