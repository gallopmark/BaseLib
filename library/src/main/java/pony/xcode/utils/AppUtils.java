package pony.xcode.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;

public class AppUtils {

    /*版本更新 apk存放的目录  放到cacheDir文件夹6.0以下无法调起安装（出现解析错误，解析程序包时出现问题）*/
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getDefaultApkPath(Context context) {
        File fileDir = context.getApplicationContext().getFilesDir();
        String path;
        if (fileDir != null) {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            path = fileDir.getAbsolutePath() + File.separator + "apk";
        } else {
            fileDir = context.getApplicationContext().getCacheDir();
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            path = context.getApplicationContext().getCacheDir() + File.separator + "apk";
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static void installApp(Context context, String apkPath) {
        if (!TextUtils.isEmpty(apkPath)) {
            installApp(context, new File(apkPath));
        }
    }

    public static void installApp(Context context, File file) {
        if (!isFileExists(file)) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data;
            String type = "application/vnd.android.package-archive";
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                data = Uri.fromFile(file);
            } else {
                String authority = context.getApplicationContext().getPackageName() + ".provider";
                data = FileProvider.getUriForFile(context, authority, file);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            context.grantUriPermission(context.getApplicationContext().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(data, type);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    private static boolean isFileExists(final File file) {
        return file != null && file.exists();
    }

    public static boolean canInstallApk(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return context.getApplicationContext().getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /* 后面跟上包名，可以直接跳转到对应APP的未知来源权限设置界面。使用startActivityForResult
    是为了在关闭设置界面之后，获取用户的操作结果，然后根据结果做其他处理*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void startUnknownAppSourceSetting(@NonNull Activity activity, int requestCode) {
        //注意这个是8.0新API
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
