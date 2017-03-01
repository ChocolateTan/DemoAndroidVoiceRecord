package com.don.voice.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by DON on 17/03/01.
 */

public class CommonUtils {
  private static final String TAG = CommonUtils.class.getSimpleName();

  public static void setVoiceViewData(Context ctx, String fileName, List<Float> list) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
    sp.edit().putString(fileName, new Gson().toJson(list)).commit();
    Log.i(TAG, "fileName=" + fileName);
    Log.i(TAG, "setVoiceViewData=" + new Gson().toJson(list));
  }

  public static List<Float> getVoiceViewData(Context ctx, String fileName) {
    Log.i(TAG, "fileName=" + fileName);
    try {
      SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
      String json = sp.getString(fileName, "");
      Type clazz = new TypeToken<List<Float>>() {
      }.getType();
      List<Float> list = new Gson().fromJson(json, clazz);
      Log.i(TAG, "getVoiceViewData=" + json);
      return list;
    } catch (Exception e) {
      e.printStackTrace();
      Log.i(TAG, "getVoiceViewData=null");
      return null;
    }
  }
}
