package pony.xcode.common;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import pony.xcode.base.ActivityResultCallback;

public class ActivityResultProcessor {

    private int mActivityRequestCode;
    /*activity result回调*/
    private ActivityResultCallback mActivityResultCallback;

    private ActivityResultProcessor(int requestCode, @Nullable ActivityResultCallback callback) {
        this.mActivityRequestCode = requestCode;
        this.mActivityResultCallback = callback;
    }

    public static ActivityResultProcessor obtain(int requestCode, @Nullable ActivityResultCallback callback) {
        return new ActivityResultProcessor(requestCode, callback);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == mActivityRequestCode && mActivityResultCallback != null) {
            if (resultCode == Activity.RESULT_OK) {
                mActivityResultCallback.onResultOk(requestCode, data);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mActivityResultCallback.onResultCanceled(requestCode, data);
            }
        }
    }
}
