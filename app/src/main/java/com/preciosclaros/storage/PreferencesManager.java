package com.preciosclaros.storage;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.gson.Gson;


public class PreferencesManager {
    private static final String PREFERENCES_NAME = "pref_aa_2000_";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER = "KEY_USER";
    private static final String KEY_SYNCHRONIZED  = "KEY_SYNCHRONIZED";
    private static final String KEY_UUID = "KEY_UUID";
    private static final String KEY_AIRPORT = "KEY_AIRPORT";
    private static final String KEY_SCREEN_MOBILE = "KEY_SCREEN_MOBILE";

    private static void removeValue(Context context, String key){
        getSharedPrefrences(context).edit().remove(key).commit();
    }

    private static SharedPreferences getSharedPrefrences(Context context){
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static String getStringFromPreferences(Context context, String key){
        return getStringFromPreferences(context, key, "");
    }

    private static String getStringFromPreferences(Context context, String key, String defaulValue){
        return getSharedPrefrences(context).getString(key, defaulValue);
    }

    private static Long getLongFromPreferences(Context context, String key){
        if (getSharedPrefrences(context).contains(key)){
            return getSharedPrefrences(context).getLong(key, 0);
        }else{
            return null;
        }
    }

    private static Integer getIntFromPreferences(Context context, String key){
        if (getSharedPrefrences(context).contains(key)){
            return getSharedPrefrences(context).getInt(key, 0);
        }else{
            return null;
        }
    }

    private static boolean getBooleanFromPreferences(Context context, String key, Boolean defValue){
        return getSharedPrefrences(context).getBoolean(key, defValue);
    }

    private static void saveStringInPreferences(Context context, String key, String value){
        Editor editor = getSharedPrefrences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static void saveIntInPreferences(Context context, String key, int value){
        Editor editor = getSharedPrefrences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static void removeKeyInPreferences(Context context, String key){
        Editor editor = getSharedPrefrences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    private static void saveLongInPreferences(Context context, String key, Long value){
        Editor editor = getSharedPrefrences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private static void saveBooleanInPreferences(Context context, String key, boolean value) {
        Editor editor = getSharedPrefrences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getToken(Context context){
        return getStringFromPreferences(context, KEY_TOKEN);
    }

    public static void saveToken(Context context, String token){
        saveStringInPreferences(context, KEY_TOKEN, token);
    }



    public static String getUUID(Context context){
        return getStringFromPreferences(context, KEY_UUID, null);
    }

    public static void saveUUID(Context context, String uuid){
        saveStringInPreferences(context, KEY_UUID, uuid);
    }

    /*public static Airport getAirport(Context context){
        try{
            return new Gson().fromJson(getStringFromPreferences(context, KEY_AIRPORT, null), Airport.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static void saveAirport(Context context, Airport airport){
        saveStringInPreferences(context, KEY_AIRPORT, new Gson().toJson(airport));
    }*/

    public static  void saveScreenType(Context context, int mobile){
        saveIntInPreferences(context,KEY_SCREEN_MOBILE,mobile);
    }

    public static int getScreenType(Context context){
        if(getIntFromPreferences(context,KEY_SCREEN_MOBILE) == null){
            return 0;
        }
        return getIntFromPreferences(context,KEY_SCREEN_MOBILE);
    }
}

