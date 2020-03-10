package pony.xcode.base;

import androidx.annotation.NonNull;

/*权限申请回调*/
public abstract class PermissionRequestCallback {
    /*某个权限被授权*/
    public abstract void onGranted(@NonNull String permission, int grantResult);

    /*权限是否全部申请通过*/
    public void onAllGranted(@NonNull String[] permissions) {

    }

    /*某个权限不允许或者用户点击禁止不再提示*/
    public void onDenied(@NonNull String permission, boolean prohibited) {

    }
}
