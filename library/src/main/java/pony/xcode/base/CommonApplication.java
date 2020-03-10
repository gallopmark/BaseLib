package pony.xcode.base;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class CommonApplication extends MultiDexApplication {
    private Set<Activity> mActivities;
    private WeakReference<Activity> mActivityWeakRef;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new IActivityLifecycleImpl());
    }

    private class IActivityLifecycleImpl implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            addActivity(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
            mActivityWeakRef = new WeakReference<>(activity);
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            removeActivity(activity);
        }
    }

    //使用场景--在服务中实时弹出被挤掉的对话框，就需要获取当前的Activity
    @Nullable
    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (mActivityWeakRef != null) {
            currentActivity = mActivityWeakRef.get();
        }
        return currentActivity;
    }

    /**
     * 添加activity
     */
    public void addActivity(Activity act) {
        if (mActivities == null) {
            mActivities = new HashSet<>();
        }
        mActivities.add(act);
    }

    /**
     * 移除activity
     */
    public void removeActivity(Activity act) {
        if (mActivities != null) {
            mActivities.remove(act);
        }
    }

    /**
     * 退出app
     */
    public void exitApp() {
        if (mActivities != null) {
            for (Activity act : mActivities) {
                act.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
