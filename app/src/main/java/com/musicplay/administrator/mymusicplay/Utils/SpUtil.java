package com.musicplay.administrator.mymusicplay.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.musicplay.administrator.mymusicplay.Value.Value;

/**
 * Created by Administrator on 2016/11/29.
 */
public class SpUtil {

    public static void putString(Context context,String key ,String vlaue) {
        SharedPreferences sp = context.getSharedPreferences("stringValue", Context.MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, vlaue);
        edit.commit();
    }

    public static String getString(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences("stringValue", Context.MODE_APPEND);
        String result = sp.getString(key, "");
        return result;
    }

    public static void putBoolean(Context context ,String key,boolean b){
        SharedPreferences sp = context.getSharedPreferences("booleanValue", Context.MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, b);
        edit.commit();
    }
    public static boolean getBoolean(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences("booleanValue", Context.MODE_APPEND);
        boolean result = sp.getBoolean(key, false);
        return result;
    }
    public static void putInt(Context context,String key ,int vlaue) {
        SharedPreferences sp = context.getSharedPreferences("intValue", Context.MODE_APPEND);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, vlaue);
        edit.commit();
    }

    public static int getInt(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences("intValue", Context.MODE_APPEND);
        int result = sp.getInt(key, 0);
        return result;
    }
}
