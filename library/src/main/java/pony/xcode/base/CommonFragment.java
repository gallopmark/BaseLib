package pony.xcode.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Random;

import pony.xcode.common.ActivityResultProcessor;
import pony.xcode.common.PermissionResultProcessor;
import pony.xcode.system.SystemTintHelper;
import pony.xcode.system.ToastCompat;

/*androidx-BaseFragment*/
public abstract class CommonFragment extends Fragment {

    protected Context mContext;
    protected View mContentView;
    protected boolean mViewInflated;
    /*默认请求码，随机生成*/
    protected final int DEFAULT_REQUEST_CODE = new Random().nextInt(65535);

    private ActivityResultProcessor mActivityResultProcessor;

    private PermissionResultProcessor mPermissionResultProcessor;

    private ToastCompat mToastCompat;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutResourceId() != 0) {
            if (mContentView == null) {
                mContentView = inflater.inflate(getLayoutResourceId(), container, false);
                setup(savedInstanceState);
            }
            mViewInflated = true;
            return mContentView;
        } else {
            mViewInflated = true;
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @LayoutRes
    protected abstract int getLayoutResourceId();

    /*onViewCreated()里调用*/
    protected void setup(@Nullable Bundle savedInstanceState) {

    }

    @NonNull
    public View getContentView() {
        if (mContentView == null)
            throw new NullPointerException("You have not set the layout file yet");
        return mContentView;
    }

    public <T extends View> T obtainView(@IdRes int id) {
        return mContentView.findViewById(id);
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
    private void setupStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View statusView = mContentView.findViewById(R.id.android_status_bar);
            if (statusView != null && color != 0) {
                ViewGroup.LayoutParams params = statusView.getLayoutParams();
                params.height = getStatusBarHeight();
                statusView.setBackgroundColor(color);
            }
        } else {
            Log.d("xcode", "This device does not support setStatusBarColor");
        }
    }

    /*获取状态栏高度*/
    public int getStatusBarHeight() {
        return SystemTintHelper.getStatusBarHeight(mContext);
    }

    /*当设置全屏沉浸状态栏时，一般使用此方法将toolbar等设置topMargin为statusHeight*/
    public void addMarginTopEqualStatusBarHeight(@NonNull View view) {
        SystemTintHelper.addMarginTopEqualStatusBarHeight(mContext, view);
    }

    public void subtractMarginTopEqualStatusBarHeight(@NonNull View view) {
        SystemTintHelper.subtractMarginTopEqualStatusBarHeight(mContext, view);
    }

    /*获取color*/
    public int getColorCompat(@ColorRes int id) {
        return ContextCompat.getColor(mContext, id);
    }

    /*获取string数组 values(string-array)*/
    public String[] getStringArray(@ArrayRes int id) {
        return getResources().getStringArray(id);
    }

    /*获取drawable*/
    public Drawable getDrawableCompat(@DrawableRes int id) {
        return ContextCompat.getDrawable(mContext, id);
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
        return ContextCompat.getColorStateList(mContext, id);
    }

    public void openActivity(Intent intent) {
        if (getActivity() == null) return;
        startActivity(intent);
    }

    /*启动activity*/
    public void openActivity(@NonNull final Class<? extends Activity> clz) {
        openActivity(clz, null);
    }

    /*启动activity，带bundle参数*/
    public void openActivity(@NonNull final Class<? extends Activity> clz, @Nullable Bundle extras) {
        if (getActivity() == null) return;
        Intent intent = new Intent(mContext, clz);
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
        if (getActivity() == null) return;
        if (mActivityResultProcessor == null) {
            mActivityResultProcessor = ActivityResultProcessor.obtain(requestCode, callback);
        }
        Intent intent = new Intent(mContext, clz);
        if (extras != null) {
            intent.putExtras(extras);
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        requestPermissions(permissions, requestCode);
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
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).startFragment(containerId, fragment, tag, enterAnim, outAnim);
        }
    }

    /*remove fragment*/
    public void removeFragment(Fragment fragment) {
        removeFragment(fragment, 0, R.anim.fragment_out_right);
    }

    /*remove fragment with anim*/
    public void removeFragment(Fragment fragment, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).removeFragment(fragment, enterAnim, outAnim);
        }
    }

    public void showLoading() {
        showLoading(null);
    }

    public void showLoading(@Nullable CharSequence message) {
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).showLoading(message);
        }
    }

    public void dismissLoading() {
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).dismissLoading();
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
        mToastCompat = ToastCompat.from(mContext);
        mToastCompat.with(text).setGravity(gravity).setDuration(duration).show();
    }

    /*fragment中点击返回处理*/
    protected void onBackPressed() {
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        mViewInflated = false;
        if (mContext instanceof CommonActivity) {
            ((CommonActivity) mContext).destroy();
        }
        super.onDestroyView();
    }
}
