package com.get.current.location.in.java.utilities.permissionutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import java.util.List;

public class PermissionSettingIntent {

    private PermissionSettingIntent() {
        throw new UnsupportedOperationException("Should not create instance of PermissionSettingIntent class. Please use as static..");
    }

    /**
     * Automatically select the most suitable permission setting intent according to the incoming permissions
     */
    public static Intent getIntent(Activity activity, String permission) {

        if (PermissionName.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
            return getStoragePermissionIntent(activity);
        }

        if (PermissionName.REQUEST_INSTALL_PACKAGES.equals(permission)) {
            return getInstallPermissionIntent(activity);
        }

        if (PermissionName.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return getWindowPermissionIntent(activity);
        }

        if (PermissionName.NOTIFICATION_SERVICE.equals(permission)) {
            return getNotifyPermissionIntent(activity);
        }

        if (PermissionName.WRITE_SETTINGS.equals(permission)) {
            return getSettingPermissionIntent(activity);
        }

        return getApplicationDetailsIntent(activity);
    }

    /**
     * This method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param intent  The Intent to check for availability.
     * @return True if an Intent with the specified action can be sent and
     * responded to, false otherwise.
     */
    private static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Get the intent of the application details interface
     */
    private static Intent getApplicationDetailsIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * Get installation permission setting interface intention
     */
    private static Intent getInstallPermissionIntent(Context context) {
        Intent intent = null;
        if (ManagePermission.isAndroid8()) {
            intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !isIntentAvailable(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * Get the intent of the floating window permission setting interface
     */
    private static Intent getWindowPermissionIntent(Context context) {
        Intent intent = null;
        if (ManagePermission.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }

        if (intent == null || !isIntentAvailable(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * Get the intent of the notification bar permission setting interface
     */
    private static Intent getNotifyPermissionIntent(Context context) {
        Intent intent = null;
        if (ManagePermission.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            /* intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid); */
        }
        if (intent == null || !isIntentAvailable(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * Get the intention of the system settings permission interface
     */
    private static Intent getSettingPermissionIntent(Context context) {
        Intent intent = null;
        if (ManagePermission.isAndroid6()) {
            intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !isIntentAvailable(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    /**
     * Get the intent of the storage permission setting interface
     */
    private static Intent getStoragePermissionIntent(Context context) {
        Intent intent = null;
        if (ManagePermission.isAndroid11()) {
            intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        }
        if (intent == null || !isIntentAvailable(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }
}