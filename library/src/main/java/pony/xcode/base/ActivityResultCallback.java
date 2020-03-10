package pony.xcode.base;

import android.content.Intent;

import androidx.annotation.Nullable;

public abstract class ActivityResultCallback {
    public abstract void onResultOk(int requestCode, @Nullable Intent data);

    public void onResultCanceled(int requestCode, @Nullable Intent data) {
    }
}
