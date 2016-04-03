package com.boha.malengagolf.library.util;

import android.app.Activity;
import android.util.Log;

import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.activities.MGApp;


public class ThemeChooser {


    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void setTheme(Activity activity) {

        int theme = SharedUtil.getThemeSelection(activity);
        Log.e("ThemeChooser","### theme about to be applied: " + theme);
        switch (theme) {

            case MGApp.THEME_BLUE:
                activity.setTheme(R.style.BlueThemeTwo);
                break;
            case MGApp.THEME_INDIGO:
                activity.setTheme(R.style.IndigoTheme);
                break;
            case MGApp.THEME_RED:
                activity.setTheme(R.style.RedTheme);
                break;
            case MGApp.THEME_TEAL:
                activity.setTheme(R.style.TealTheme);
                break;
            case MGApp.THEME_BLUE_GRAY:
                activity.setTheme(R.style.BlueGrayTheme);
                break;
            case MGApp.THEME_ORANGE:
                activity.setTheme(R.style.OrangeTheme);
                break;
            case MGApp.THEME_PINK:
                activity.setTheme(R.style.PinkTheme);
                break;
            case MGApp.THEME_CYAN:
                activity.setTheme(R.style.CyanTheme);
                break;
            case MGApp.THEME_GREEN:
                activity.setTheme(R.style.GreenTheme);
                break;
            case MGApp.THEME_GREY:
                activity.setTheme(R.style.GreyTheme);
                break;
            case MGApp.THEME_LIGHT_GREEN:
                activity.setTheme(R.style.LightGreenTheme);
                break;
            case MGApp.THEME_LIME:
                activity.setTheme(R.style.LimeTheme);
                break;
            case MGApp.THEME_PURPLE:
                activity.setTheme(R.style.PurpleTheme);
                break;
            case MGApp.THEME_AMBER:
                activity.setTheme(R.style.AmberTheme);
                break;
            case MGApp.THEME_BROWN:
                activity.setTheme(R.style.BrownTheme);
                break;
            default:
                Log.d("ThemeChooser", "### no theme selected, none set");
                break;
        }

    }
}