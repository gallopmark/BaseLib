package pony.xcode.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Random;

import pony.xcode.common.ActivityResultProcessor;
import pony.xcode.common.ActivityThemeProcessor;
import pony.xcode.common.FragmentProcessor;
import pony.xcode.common.PermissionResultProcessor;
import pony.xcode.dialog.LoadingDialog;
import pony.xcode.system.SystemTintHelper;
import pony.xcode.system.ToastCompat;

/*androidx-BaseActivity*/
public abstract class CommonActivity extends AppCompatActivity {

    /*activity动画适配*/
    private ActivityThemeProcessor mThemeProcessor;

    //    /*默认请求码，随机生成(startActivityForResult请求码-requestCode的取值范围是：0 to 65535*/
    protected final int DEFAULT_REQUEST_CODE = new Random().nextInt(65535);

    private ActivityResultProcessor mActivityResultProcessor;

    private PermissionResultProcessor mPermissionResultProcessor;

    private FragmentProcessor mFragmentProcessor;

    /*加载loading对话框*/
    private Dialog mLoadingDialog;

    private ToastCompat mToastCompat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setRequestedOrientation(getScreenOrientation());
        setScreenStyle();
        displayTheme();
        super.onCreate(savedInstanceState);
        obtainFragmentProcessor();
        setContentView();
        applyPresenter();
        setup(savedInstanceState);
    }

    /*setContentView之前调用，可以设置theme等属性*/
    protected void setScreenStyle() {
        if (isFullScreen()) {
            setFullscreen();
        } else {
            if (isTransparentStatusBar()) {
                setTransparentStatusBar();
            } else {
                setStatusBarLightMode(true);
            }
        }
    }

    /*适配activity启动动画问题*/
    private void displayTheme() {
        mThemeProcessor = ActivityThemeProcessor.display(this);
    }

    private void obtainFragmentProcessor() {
        mFragmentProcessor = FragmentProcessor.from(this);
    }

    /*重写此方法设置屏幕方向-默认强制竖屏*/
    protected int getScreenOrientation() {
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @LayoutRes
    protected abstract int getLayoutResourceId();

    /*设置布局文件*/
    protected void setContentView() {
        if (getLayoutResourceId() != 0) {
            setContentView(getLayoutResourceId());
        }
    }

    void applyPresenter() {

    }

    protected void setup(@Nullable Bundle savedInstanceState) {

    }

    /*是否全拼*/
    protected boolean isFullScreen() {
        return false;
    }

    /*设置全屏*/
    protected void setFullscreen() {
        SystemTintHelper.setFullscreen(this);
    }

    /*取消全屏*/
    protected void clearFullscreen() {
        SystemTintHelper.clearFullscreen(this);
    }

    protected boolean isTransparentStatusBar() {
        return false;
    }

    /*设置全屏沉浸透明状态栏-建议调用addMarginTopEqualStatusBarHeight方法使得布局腾出状态栏的高度*/
    public void setTransparentStatusBar() {
        SystemTintHelper.setTransparentStatusBar(this);
    }

    /*状态栏亮色模式*/
    public void setStatusBarLightMode() {
        setStatusBarLightMode(false);
    }

    /*是否设置状态栏白底 v6.0以上*/
    public void setStatusBarLightMode(boolean includeMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && includeMode) {
            setStatusBarColor(Color.WHITE);
        }
        SystemTintHelper.setStatusBarLightMode(this);
    }

    /*修改状态栏颜色 比如ContextCompat.getColor(context,R.color.colorPrimaryDark)*/
    public void setStatusBarColor(@ColorInt int colorId) {
        setupStatusBarColor(colorId);
    }

    /*修改状态栏颜色 比如传入R.color.colorPrimaryDark*/
    public void setStatusBarColorResource(@ColorRes int colorResId) {
        setupStatusBarColor(getColorCompat(colorResId));
    }

    /*android 5.0以下不支持设置状态栏颜色-4.4～5.0之间生成一个和状态栏大小相同的矩形条*/
    private void setupStatusBarColor(@ColorInt int colorId) {
        if (!isTransparentStatusBar()) {
            SystemTintHelper.setStatusBarColor(this, colorId);
        } else {
            SystemTintHelper.setStatusViewColor(this, findViewById(R.id.android_status_bar), colorId);
        }
    }

    /*获取状态栏高度*/
    public int getStatusBarHeight() {
        return SystemTintHelper.getStatusBarHeight(this);
    }

    /*当设置全屏沉浸状态栏时，一般使用此方法将toolbar等设置topMargin为statusHeight*/
    public void addMarginTopEqualStatusBarHeight(@NonNull View view) {
        SystemTintHelper.addMarginTopEqualStatusBarHeight(this, view);
    }

    public void subtractMarginTopEqualStatusBarHeight(@NonNull View view) {
        SystemTintHelper.subtractMarginTopEqualStatusBarHeight(this, view);
    }

    /*获取color*/
    public int getColorCompat(@ColorRes int id) {
        return ContextCompat.getColor(this, id);
    }

    /*获取string数组 values(string-array)*/
    public String[] getStringArray(@ArrayRes int id) {
        return getResources().getStringArray(id);
    }

    /*获取drawable*/
    public Drawable getDrawableCompat(@DrawableRes int id) {
        return ContextCompat.getDrawable(this, id);
    }

    /*获取dimens value*/
    public float getDimension(@DimenRes int id) {
        return getResources().getDimension(id);
    }

    /*将getDimension结果转换为int，并且小数部分四舍五入*/
    public int getDimensionPixelSize(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    /*直接截断小数位，即取整其实就是把float强制转化为int，注意不是四舍五入*/
    public int getDimensionPixelOffset(@DimenRes int id) {
        return getResources().getDimensionPixelOffset(id);
    }

    public int[] getIntArray(@ArrayRes int id) {
        return getResources().getIntArray(id);
    }

    public ColorStateList getColorStateListCompat(@ColorRes int id) {
        return ContextCompat.getColorStateList(this, id);
    }

    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    /*启动activity*/
    public void openActivity(@NonNull final Class<? extends Activity> clz) {
        openActivity(clz, null);
    }

    /*启动activity，带bundle参数*/
    public void openActivity(@NonNull final Class<? extends Activity> clz, @Nullable Bundle extras) {
        Intent intent = new Intent(this, clz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivity(intent);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz) {
        openActivityForResult(clz, null);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz, @Nullable ActivityResultCallback callback) {
        openActivityForResult(clz, null, DEFAULT_REQUEST_CODE, callback);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz, @IntRange(from = 0, to = 65535) int requestCode) {
        openActivityForResult(clz, requestCode, null);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz, @IntRange(from = 0, to = 65535) int requestCode, @Nullable ActivityResultCallback callback) {
        openActivityForResult(clz, null, requestCode, callback);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz, @Nullable Bundle extras, @IntRange(from = 0, to = 65535) int requestCode) {
        openActivityForResult(clz, extras, requestCode, null);
    }

    public void openActivityForResult(@NonNull final Class<? extends Activity> clz, @Nullable Bundle extras, @IntRange(from = 0, to = 65535) int requestCode, @Nullable ActivityResultCallback callback) {
        if (mActivityResultProcessor == null) {
            mActivityResultProcessor = ActivityResultProcessor.obtain(requestCode, callback);
        }
        Intent intent = new Intent(this, clz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mActivityResultProcessor != null) {
            mActivityResultProcessor.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void requestPermission(String permission, @Nullable PermissionRequestCallback callback) {
        requestPermissions(new String[]{permission}, DEFAULT_REQUEST_CODE, callback);
    }

    public void requestPermissions(final @NonNull String[] permissions, @Nullable PermissionRequestCallback callback) {
        requestPermissions(permissions, DEFAULT_REQUEST_CODE, callback);
    }

    public void requestPermissions(final @NonNull String[] permissions, @IntRange(from = 0, to = 65535) int requestCode, @Nullable PermissionRequestCallback callback) {
        if (mPermissionResultProcessor == null) {
            mPermissionResultProcessor = PermissionResultProcessor.obtain(requestCode, callback);
        }
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionResultProcessor != null) {
            mPermissionResultProcessor.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    /*add fragment*/
    public void startFragment(@IdRes int containerId, Fragment fragment) {
        startFragment(containerId, fragment, 0, 0);
    }

    /*加载默认动画-fragment右侧滑入*/
    public void startFragment(@IdRes int containerId, Fragment fragment, boolean smoothScroll) {
        if (smoothScroll) {
            startFragment(containerId, fragment, R.anim.fragment_from_right, 0);
        } else {
            startFragment(containerId, fragment);
        }
    }

    /*add fragment with anim*/
    public void startFragment(@IdRes int containerId, Fragment fragment, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        startFragment(containerId, fragment, null, enterAnim, outAnim);
    }

    /*add fragment with tag*/
    public void startFragment(@IdRes int containerId, Fragment fragment, @Nullable String tag) {
        startFragment(containerId, fragment, tag, 0, 0);
    }

    /*取消fragment动画传入参数 enterAnim = 0 && outAnim == 0*/
    public void startFragment(@IdRes int containerId, Fragment fragment, @Nullable String tag, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        mFragmentProcessor.startFragment(containerId, fragment, tag, enterAnim, outAnim);
    }

    public void addFragmentToList(Fragment fragment) {
        mFragmentProcessor.addFragmentToList(fragment);
    }

    /*remove fragment*/
    public void removeFragment(Fragment fragment) {
        mFragmentProcessor.removeFragment(fragment);
    }

    /*remove fragment with anim*/
    public void removeFragment(Fragment fragment, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        mFragmentProcessor.removeFragment(fragment, enterAnim, outAnim);
    }

    public void removeFragmentFromList(Fragment fragment) {
        mFragmentProcessor.removeFragmentFromList(fragment);
    }

    /*是否支持多层fragment嵌套，返回上一层，子类重写该方法并返回false则直接退出当前activity*/
    protected boolean isSupportHandleBack() {
        return true;
    }

    public boolean canHandleBack() {
        return mFragmentProcessor.canHandleBack();
    }

    /*获取当前activity中addFragment or replaceFragment fragment总数*/
    @NonNull
    public List<Fragment> getFragments() {
        return mFragmentProcessor.getFragments();
    }

    /*显示加载等待框*/
    public void showLoading() {
        showLoading(null);
    }

    public void showLoading(@Nullable CharSequence message) {
        dismissLoading();
        if (mLoadingDialog == null) {
            mLoadingDialog = getLoadingDialog();
        }
        if (mLoadingDialog instanceof LoadingDialog) {
            ((LoadingDialog) mLoadingDialog).setMessage(message);
        }
        mLoadingDialog.show();
    }

    @NonNull
    protected Dialog getLoadingDialog() {
        return new LoadingDialog(this);
    }

    public void dismissLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public void showShortToast(@StringRes int resId) {
        showShortToast(resId, -1);
    }

    public void showShortToast(@StringRes int resId, int gravity) {
        showShortToast(getString(resId), gravity);
    }

    public void showShortToast(@Nullable CharSequence text) {
        showShortToast(text, -1);
    }

    public void showShortToast(@Nullable CharSequence text, int gravity) {
        showToast(text, gravity, Toast.LENGTH_SHORT);
    }

    public void showLongToast(@StringRes int resId) {
        showLongToast(resId, -1);
    }

    public void showLongToast(@StringRes int resId, int gravity) {
        showLongToast(getString(resId), gravity);
    }

    public void showLongToast(@Nullable CharSequence text) {
        showLongToast(text, -1);
    }

    public void showLongToast(@Nullable CharSequence text, int gravity) {
        showToast(text, gravity, Toast.LENGTH_LONG);
    }

    public void showToast(@Nullable CharSequence text, int gravity, int duration) {
        if (TextUtils.isEmpty(text)) return;
        if (mToastCompat != null) {
            mToastCompat.cancel();
        }
        mToastCompat = ToastCompat.from(this);
        mToastCompat.with(text).setGravity(gravity).setDuration(duration).show();
    }

    @Override
    public void onBackPressed() {
        if (!isSupportHandleBack()) {
            super.onBackPressed();
        } else {
            if (canHandleBack())
                super.onBackPressed();
        }
    }

    /*解决activity退出动画无效问题*/
    @Override
    public void finish() {
        super.finish();
        if (mThemeProcessor != null) {
            mThemeProcessor.exit();
        }
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    void destroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
