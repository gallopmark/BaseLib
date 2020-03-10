package pony.xcode.system;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import pony.xcode.base.PermissionRequestCallback;

public class PermissionResultHelper {

    public static void apply(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults, @Nullable final PermissionRequestCallback callback) {
        if (callback == null) return;
        int deniedLength = 0;
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                callback.onGranted(permission, grantResults[i]);
            } else {
                deniedLength += 1;
                callback.onDenied(permission, !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission));
            }
        }
        if (deniedLength == 0) {
            callback.onAllGranted(permissions);
        }
    }

    public static void apply(Fragment fragment, @NonNull String[] permissions, @NonNull int[] grantResults, @Nullable final PermissionRequestCallback callback) {
        if (callback == null) return;
        int deniedLength = 0;
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                callback.onGranted(permission, grantResults[i]);
            } else {
                deniedLength += 1;
                callback.onDenied(permission, !fragment.shouldShowRequestPermissionRationale(permission));
            }
        }
        if (deniedLength == 0) {
            callback.onAllGranted(permissions);
        }
    }
}
