package com.boha.malengagolf.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.boha.malengagolf.library.R;
import com.boha.malengagolf.library.data.*;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Random;

public class SharedUtil {
    public static final String SCROLL_INDEX = "scrollIndex",
        SCROLL_TIME = "sTime", SESSION_ID = "sesID";

    public static void setScrollIndex(Context ctx, int index) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        Editor ed = sp.edit();
        ed.putInt(SCROLL_INDEX, index);
        ed.putLong(SCROLL_TIME, new Date().getTime());
        ed.commit();
        //Log.w("SharedUtil", "#### scroll index saved: " + index);

    }
    public static int getScrollIndex(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int j = sp.getInt(SCROLL_INDEX, 0);

        return j;
    }
    public static void setSessionID(Context ctx, String sessionID) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);

        Editor ed = sp.edit();
        ed.putString(Statics.SESSION_ID, sessionID);
        ed.commit();
        Log.w("SharedUtil", "#### web socket session ID saved: " + sessionID);

    }
    public static String getSessionID(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String j = sp.getString(Statics.SESSION_ID, null);

        return j;
    }

    public static void saveLocation(Context ctx, double latitude, double longitude) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        Editor ed = sp.edit();
        ed.putLong(Constants.LAST_LATITUDE, (long) (latitude * 1E6));
        ed.putLong(Constants.LAST_LONGITUDE, (long) (longitude * 1E6));
        ed.commit();
    }
    public static double[] getLastLocation(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        long lat = sp.getLong(Constants.LAST_LATITUDE, 0);
        long lng = sp.getLong(Constants.LAST_LATITUDE, 0);
        double latitude = (lat /1E6);
        double longitude = (lng /1E6);
        double[] loc = new double[2];
        loc[0] = latitude;
        loc[1] = longitude;
        return loc;
    }

    public static void saveSplashIndex(Context ctx, int index) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        Editor ed = sp.edit();
        ed.putInt(Constants.SPLASH_INDEX, index);
        ed.commit();
    }
    public static int getSplashIndex(Context ctx) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int i = sp.getInt(Constants.SPLASH_INDEX, 0);
        i++;
        if (i > 27) {
            i = 0;
        }
        saveSplashIndex(ctx,i);
        return i;
    }


    public static Drawable getRandomSplash(Context ctx) {
        Random rand = new Random(System.currentTimeMillis());
        int index = rand.nextInt(27);
        return getSplash(ctx,index);
    }
    public static Drawable getSplash(Context ctx, int index) {

        switch (index) {
            case 0:
                return ctx.getResources().getDrawable(R.drawable.golf_pic1);
            case 1:
                return ctx.getResources().getDrawable(R.drawable.golf_pic2);
            case 2:
                return ctx.getResources().getDrawable(R.drawable.golf_pic3);
            case 3:
                return ctx.getResources().getDrawable(R.drawable.golf_pic4);
            case 4:
                return ctx.getResources().getDrawable(R.drawable.golf_pic5);
            case 5:
                return ctx.getResources().getDrawable(R.drawable.golf_pic6);
            case 6:
                return ctx.getResources().getDrawable(R.drawable.golf_pic7);
            case 7:
                return ctx.getResources().getDrawable(R.drawable.golf_pic8);
            case 8:
                return ctx.getResources().getDrawable(R.drawable.golf_pic9);
            case 9:
                return ctx.getResources().getDrawable(R.drawable.golf_pic10);
            case 10:
                return ctx.getResources().getDrawable(R.drawable.golf_pic11);
            case 11:
                return ctx.getResources().getDrawable(R.drawable.golf_pic12);
            case 12:
                return ctx.getResources().getDrawable(R.drawable.golf_pic13);
            case 13:
                return ctx.getResources().getDrawable(R.drawable.golf_pic14);
            case 14:
                return ctx.getResources().getDrawable(R.drawable.golf_pic15);
            case 15:
                return ctx.getResources().getDrawable(R.drawable.golf_pic16);
            case 16:
                return ctx.getResources().getDrawable(R.drawable.golf_pic17);
            case 17:
                return ctx.getResources().getDrawable(R.drawable.golf_pic18);
            case 18:
                return ctx.getResources().getDrawable(R.drawable.golf_pic19);
            case 19:
                return ctx.getResources().getDrawable(R.drawable.golf_pic20);
            case 20:
                return ctx.getResources().getDrawable(R.drawable.golf_pic21);
            case 21:
                return ctx.getResources().getDrawable(R.drawable.golf_pic22);
            case 22:
                return ctx.getResources().getDrawable(R.drawable.golf_pic23);
            case 23:
                return ctx.getResources().getDrawable(R.drawable.golf_pic24);
            case 24:
                return ctx.getResources().getDrawable(R.drawable.golf_pic25);
            case 25:
                return ctx.getResources().getDrawable(R.drawable.golf_pic26);
            case 26:
                return ctx.getResources().getDrawable(R.drawable.golf_pic27);
            case 27:
                return ctx.getResources().getDrawable(R.drawable.golf_pic28);


        }
        return ctx.getResources().getDrawable(R.drawable.golf_pic22);
    }
    public static void saveProvince(Context ctx, ProvinceDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.PROVINCE_JSON, x);
        ed.commit();
    }
    public static ProvinceDTO getProvince(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(Constants.PROVINCE_JSON, null);
        ProvinceDTO prx = null;
        if (adm != null) {
            prx = gson.fromJson(adm, ProvinceDTO.class);

        }
        return prx;
    }

    public static void saveAdministration(Context ctx, AdministratorDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.ADMINISTRATOR_JSON, x);
        ed.commit();
    }
	public static AdministratorDTO getAdministrator(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.ADMINISTRATOR_JSON, null);
		AdministratorDTO admin = null;
		if (adm != null) {
			admin = gson.fromJson(adm, AdministratorDTO.class);

		}
		return admin;
	}

    public static void saveAppUser(Context ctx, AppUserDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.APPUSER_JSON, x);
        ed.commit();
    }
    public static AppUserDTO getAppUser(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(Constants.APPUSER_JSON, null);
        AppUserDTO p = null;
        if (adm != null) {
            p = gson.fromJson(adm, AppUserDTO.class);

        }
        return p;
    }
    public static void savePlayer(Context ctx, PlayerDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.PLAYER_JSON, x);
        ed.commit();
    }
	public static PlayerDTO getPlayer(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.PLAYER_JSON, null);
		PlayerDTO p = null;
		if (adm != null) {
			p = gson.fromJson(adm, PlayerDTO.class);

		}
		return p;
	}

	public static void saveGolfGroup(Context ctx, GolfGroupDTO dto) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.GOLF_GROUP_JSON, x);
		ed.commit();
        Log.e("SharedUtil", "%%%%% GolfGroup: " + dto.getGolfGroupName() + " saved in SharedPreferences");
	}

	public static GolfGroupDTO getGolfGroup(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.GOLF_GROUP_JSON, null);
		GolfGroupDTO golfGroup = null;
		if (adm != null) {
			golfGroup = gson.fromJson(adm, GolfGroupDTO.class);

		}
		return golfGroup;
	}

    public static void saveParent(Context ctx, ParentDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.PARENT_JSON, x);
        ed.commit();
    }
	public static ParentDTO getParent(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.PARENT_JSON, null);
		ParentDTO dto = null;
		if (adm != null) {
			dto = gson.fromJson(adm, ParentDTO.class);

		}
		return dto;
	}
    public static void saveScorer(Context ctx, ScorerDTO dto) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.SCORER_JSON, x);
        ed.commit();
    }
	public static ScorerDTO getScorer(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String adm = sp.getString(Constants.SCORER_JSON, null);
		ScorerDTO cls = null;
		if (adm != null) {
			cls = gson.fromJson(adm, ScorerDTO.class);

		}
		return cls;
	}

	public static String getImageUri(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String s = sp.getString(Constants.IMAGE_URI, null);

		return s;
	}

	public static String getThumbUri(Context ctx) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String s = sp.getString(Constants.THUMB_URI, null);

		return s;
	}

	public static void saveImageUri(Context ctx, Uri uri) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putString(Constants.IMAGE_URI, uri.toString());
		ed.commit();
        Log.d("SharedUtil", "Image uri saved: " + uri.toString());
	}

	public static void saveThumbUri(Context ctx, Uri uri) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		Editor ed = sp.edit();
		ed.putString(Constants.THUMB_URI, uri.toString());
		ed.commit();
        Log.d("SharedUtil", "Thumb uri saved: " + uri.toString());
	}

	public static void saveTournament(Context ctx, TournamentDTO dto) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String x = gson.toJson(dto);
		Editor ed = sp.edit();
		ed.putString(Constants.CURRENT_TOURNAMENT, x);
		ed.commit();
	}
    public static TournamentDTO getTournament(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(Constants.CURRENT_TOURNAMENT, null);
        TournamentDTO cls = null;
        if (adm != null) {
            cls = gson.fromJson(adm, TournamentDTO.class);

        }
        return cls;
    }

    public void showMap(Context ctx, Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(intent);
        }
    }
    public static void saveVideoClip(Context ctx, VideoClipDTO dto) {
        VideoClipContainer vcc = null;
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String clips = sp.getString(Constants.VIDEO_CLIPS, null);
        if (clips == null) {
            vcc = new VideoClipContainer();
        } else {
            vcc = gson.fromJson(clips, VideoClipContainer.class);
        }
        vcc.getVideoClips().add(0,dto);

        String x = gson.toJson(vcc);
        Editor ed = sp.edit();
        ed.putString(Constants.VIDEO_CLIPS, x);
        ed.commit();
        Log.e("SharedUtil", "Video clip saved in prefs ");
    }
    public static void saveVideoContainer(Context ctx, VideoClipContainer dto) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String x = gson.toJson(dto);
        Editor ed = sp.edit();
        ed.putString(Constants.VIDEO_CLIPS, x);
        ed.commit();
    }
    public static VideoClipContainer getVideoContainer(Context ctx) {

        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String adm = sp.getString(Constants.VIDEO_CLIPS, null);
        VideoClipContainer vcc = null;
        if (adm != null) {
            vcc = gson.fromJson(adm, VideoClipContainer.class);

        }
        return vcc;
    }
	private static final Gson gson = new Gson();
}
