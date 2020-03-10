package pony.xcode.common;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import pony.xcode.base.PermissionRequestCallback;
import pony.xcode.system.PermissionResultHelper;

public class PermissionResultProcessor {

    private int mPermissionRequestCode;
    private PermissionRequestCallback mPermissionRequestCallback;

    private PermissionResultProcessor(int requestCode, @Nullable PermissionRequestCallback callback) {
        this.mPermissionRequestCode = requestCode;
        this.mPermissionRequestCallback = callback;
    }

    public static PermissionResultProcessor obtain(int requestCode, @Nullable PermissionRequestCallback callback) {
        return new PermissionResultProcessor(requestCode, callback);
    }

    public void onRequestPermissionsResult(@NonNull Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mPermissionRequestCode) {
            PermissionResultHelper.apply(activity, permissions, grantResults, mPermissionRequestCallback);
        }
    }

    public void onRequestPermissionsResult(@NonNull Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == mPermissionRequestCode) {
            PermissionResultHelper.apply(fragment, permissions, grantResults, mPermissionRequestCallback);
        }
    }
}
