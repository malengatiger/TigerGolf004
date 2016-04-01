package com.squareup.picasso;

import android.util.Log;

/**
 * Created by aubreyM on 2014/06/17.
 */
public class PicassoTools {
    public static void clearCache(Picasso p) {
        Log.d(LOG, "clearCache - Picasso: " + p.getSnapshot().toString());
        p.cache.clear();
        Log.w(LOG, "------ cache cleared, maybe?");
    }
    static final String LOG = "PicassoTools";
}
