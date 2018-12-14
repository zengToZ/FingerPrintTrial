package com.zz.fingerprinttrial;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionCheck {

    final public static String[] PERMISSIONS = {
            Manifest.permission.USE_FINGERPRINT,
    };

    // check permission since Android M version
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
