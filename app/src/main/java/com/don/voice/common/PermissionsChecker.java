package com.don.voice.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by DON on 17/01/12.
 */

public class PermissionsChecker {
  private static final String TAG = PermissionsChecker.class.getSimpleName();

  private PermissionsChecker() {}

  public static boolean lacksPermissions(Context context, String... permissions) {
    if (Build.VERSION.SDK_INT >= 23) {
      for (String permission : permissions) {
        if (lacksPermission(context, permission)) {
          return true;
        }
      }
      return false;
    } else { //permission is automatically granted on sdk<23 upon installation
      Log.v(TAG, "Permission is granted");
      return false;
    }
  }

  private static boolean lacksPermission(Context context, String permission) {
    if (Build.VERSION.SDK_INT >= 23) {
      return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    } else { //permission is automatically granted on sdk<23 upon installation
      Log.v(TAG, "Permission is granted");
      return false;
    }
  }
}
