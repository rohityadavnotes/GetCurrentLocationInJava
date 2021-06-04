package com.get.current.location.in.java.utilities.permissionutils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManagePermission {

    private Activity activity;

    public ManagePermission(Activity activity) {
        this.activity = activity;
    }

    /**
     * Is it Android 11.0 and above
     */
    public static boolean isAndroid11() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;
    }

    /**
     * Is it Android 10.0 and above
     */
    public static boolean isAndroid10() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    /**
     * Whether it is Android 8.0 and above
     */
    public static boolean isAndroid8() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * Whether it is Android 7.0 and above
     */
    public static boolean isAndroid7() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * Whether it is Android 6.0 and above
     */
    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Is there storage permission
     */
    public boolean hasStoragePermission() {
        if (isAndroid11()) {
            return Environment.isExternalStorageManager();
        }
        return hasPermission(PermissionName.Group.STORAGE);
    }

    /**
     * Is there installation permission
     */
    public boolean hasInstallPermission(Context context) {
        if (isAndroid8()) {
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * Is there permission for floating window
     */
    public boolean hasWindowPermission(Context context) {
        if (isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * Is there a notification bar permission
     */
    public boolean hasNotifyPermission(Context context) {
        if (isAndroid7()) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        }

        if (isAndroid6()) {
            /* Refer to the method in the Support library: NotificationManagerCompat.from(context).areNotificationsEnabled() */
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = appOps.getClass().getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
                Field field = appOps.getClass().getDeclaredField("OP_POST_NOTIFICATION");
                int value = (Integer) field.get(Integer.class);
                return ((int) method.invoke(appOps, value, context.getApplicationInfo().uid, context.getPackageName())) == AppOpsManager.MODE_ALLOWED;
            } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException | RuntimeException ignored) {
                return true;
            }
        }

        return true;
    }

    /**
     * Whether there is system setting authority
     */
    public boolean hasSettingPermission(Context context) {
        if (isAndroid6()) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    public boolean isPermissionGranted(Context context, List<String> permissions) {
        checkPermissionManifest(permissions);

        if (activity == null) {
            throw new RuntimeException("activity context is null");
        } else if (permissions == null) {
            throw new RuntimeException("permission is null");
        }
        /*
         * Marshmallow +
         * If api level greater than or equal to 23, then all permission is
         * granted at runtime time.
         */
        else if (isAndroid6()) {
            for (String permission : permissions) {
                if (!isPermissionGranted(context, permission)) {
                    return false;
                }
            }
            return true;
        }
        /*
         * Pre-Marshmallow
         * If api level less than 23, then all permission is
         * granted at install time in google play store.
         */
        else {
            /*
             * Below api level 23, always true
             */
            return true;
        }
    }

    public boolean isPermissionGranted(Context context, String permission) {
        /* If it is Android 6.0 or lower, it will be granted by default */
        if (!isAndroid6()) {
            return true;
        }

        /* Check storage permissions */
        if (PermissionName.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
            return hasStoragePermission();
        }

        /* Check installation permissions */
        if (PermissionName.REQUEST_INSTALL_PACKAGES.equals(permission)) {
            return hasInstallPermission(context);
        }

        /* Check the permission of the floating window */
        if (PermissionName.SYSTEM_ALERT_WINDOW.equals(permission)) {
            return hasWindowPermission(context);
        }

        /* Check the notification bar permissions */
        if (PermissionName.NOTIFICATION_SERVICE.equals(permission)) {
            return hasNotifyPermission(context);
        }

        /* Check system permissions */
        if (PermissionName.WRITE_SETTINGS.equals(permission)) {
            return hasSettingPermission(context);
        }

        if (!isAndroid10()) {
            /*
             * Detect the three new permissions of 10.0, if the current version does not meet the
             * minimum requirements, then use the old permissions for testing
             */
            if (PermissionName.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    PermissionName.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return true;
            }

            if (PermissionName.ACTIVITY_RECOGNITION.equals(permission)) {
                return context.checkSelfPermission(PermissionName.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (!isAndroid8()) {
            /*
             * Detect the two new permissions of 8.0, if the current version does not meet the minimum
             * requirements, then use the old permissions for detection
             */
            if (PermissionName.ANSWER_PHONE_CALLS.equals(permission)) {
                return context.checkSelfPermission(PermissionName.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
            }

            if (PermissionName.READ_PHONE_NUMBERS.equals(permission)) {
                return context.checkSelfPermission(PermissionName.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
            }
        }

        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isSpecialPermission(String permission) {
        return PermissionName.MANAGE_EXTERNAL_STORAGE.equals(permission) ||
                PermissionName.REQUEST_INSTALL_PACKAGES.equals(permission) ||
                PermissionName.SYSTEM_ALERT_WINDOW.equals(permission) ||
                PermissionName.NOTIFICATION_SERVICE.equals(permission) ||
                PermissionName.WRITE_SETTINGS.equals(permission);
    }

    /**
     * Determine whether a certain permission set contains special permissions
     */
    public boolean containsSpecialPermission(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (isSpecialPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether a permission is permanently denied
     *
     * @param permission requested permission
     */
    public boolean shouldShowRequestPermissionRationale(String permission) {
        if (!isAndroid6()) {
            return false;
        }

        /*
         * Special permissions are not counted. The application method is different from the dangerous
         * permission application method. Because there is no option for permanent rejection, false is
         * returned here.
         */
        if (isSpecialPermission(permission)) {
            return false;
        }

        if (!isAndroid10()) {
            /*
             * Detect the three new permissions of 10.0, if the current version does not meet the
             * minimum requirements, then use the old permissions for testing
             */
            if (PermissionName.ACCESS_BACKGROUND_LOCATION.equals(permission) ||
                    PermissionName.ACCESS_MEDIA_LOCATION.equals(permission)) {
                return false;
            }

            if (PermissionName.ACTIVITY_RECOGNITION.equals(permission)) {
                return activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!isAndroid8()) {
            /*
             * Detect the two new permissions of 8.0, if the current version does not meet the minimum
             * requirements, then use the old permissions for testing
             */
            if (PermissionName.ANSWER_PHONE_CALLS.equals(permission)) {
                return activity.shouldShowRequestPermissionRationale(permission);
            }

            if (PermissionName.READ_PHONE_NUMBERS.equals(permission)) {
                return activity.shouldShowRequestPermissionRationale(permission);
            }
        }

        return activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * Convert the array to ArrayList
     * <p>
     * Here to explain why you don't use Arrays.asList
     * The first is that the returned type is not java.util.ArrayList but java.util.Arrays.ArrayList
     * The second is that the returned ArrayList object is read-only, that is, no elements can be added, otherwise an exception will be thrown
     */
    public <T> ArrayList<T> asArrayList(T... array) {
        ArrayList<T> list = null;
        if (array != null) {
            list = new ArrayList<>(array.length);
            for (T t : array) {
                list.add(t);
            }
        }
        return list;
    }

    public boolean hasPermission(String... permissions) {
        return hasPermission(asArrayList(permissions));
    }

    public boolean hasPermission(List<String> permissions) {
        return isPermissionGranted(activity, permissions);
    }

    public boolean hasPermission(String[]... permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String[] group : permissions) {
            permissionList.addAll(asArrayList(group));
        }
        return isPermissionGranted(activity, permissionList);
    }

    /**
     * Get the list of all dangerous permission from android system.
     *
     * @return - List of all dangerous permission
     */
    public LinkedList<String> getDangerousPermission() {
        LinkedList<String> permissionList = new LinkedList<>();

        /* Get the permissions for the core android package */
        PackageInfo packageInfo = null;

        try {
            PackageManager packageManager = activity.getPackageManager();
            packageInfo = packageManager.getPackageInfo("android", PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        if (packageInfo.permissions != null) {
            /* For each defined permission */
            for (PermissionInfo permission : packageInfo.permissions) {
                if (permission.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) {
                    permissionList.add(permission.name);
                }
            }
        }
        return permissionList;
    }

    /**
     * Returns list of the added permissions in the manifest file
     */
    public List<String> getListOfAddedPermissionInManifest() {
        try {
            PackageManager packageManager = activity.getPackageManager();
            String[] requestedPermissions = packageManager.getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
            if (requestedPermissions != null) {
                return asArrayList(requestedPermissions);
            } else {
                return null;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    /**
     * Check whether the permission is added or not in the manifest file
     *
     * @param listOfPermissions list of permissions
     */
    public void checkPermissionsAvailableInManifestFile(List<String> listOfPermissions) {
        List<String> listOfAddedPermissionInManifest = getListOfAddedPermissionInManifest();
        if (listOfAddedPermissionInManifest != null && listOfAddedPermissionInManifest.size() != 0) {
            for (String permission : listOfPermissions) {
                if (!listOfAddedPermissionInManifest.contains(permission)) {
                    throw new RuntimeException("you must add this permission : " + permission + " to AndroidManifest");
                }
            }
        }
    }

    /**
     * Handling and optimizing outdated permissions
     */
    public void optimizeDeprecatedPermission(List<String> permission) {
        /* If this application includes Android 11 storage permissions */
        if (permission.contains(PermissionName.MANAGE_EXTERNAL_STORAGE)) {

            if (permission.contains(PermissionName.READ_EXTERNAL_STORAGE) || permission.contains(PermissionName.WRITE_EXTERNAL_STORAGE)) {
                /*
                 * Check whether there is an old version of storage permissions, if any, throw an
                 * exception directly, please do not apply for these two permissions dynamically by yourself
                 */
                throw new IllegalArgumentException("Please do not apply for these two permissions dynamically");
            }

            if (!isAndroid11()) {
                /*
                 * Automatically add the storage permission of the old version, because the old version
                 * of the system does not support applying for the storage permission of the new version
                 */
                permission.add(PermissionName.READ_EXTERNAL_STORAGE);
                permission.add(PermissionName.WRITE_EXTERNAL_STORAGE);
            }
        }

        if (permission.contains(PermissionName.ANSWER_PHONE_CALLS)) {
            if (permission.contains(PermissionName.PROCESS_OUTGOING_CALLS)) {
                /*
                 * Check whether there is an old version of the permission to answer calls, if any,
                 * throw an exception directly, please do not apply for this permission dynamically
                 */
                throw new IllegalArgumentException("Please do not apply for these two permissions dynamically");
            }

            if (!isAndroid10() && !permission.contains(PermissionName.PROCESS_OUTGOING_CALLS)) {
                /*
                 * Automatically add the permission of the old version to answer calls, because the
                 * old version of the system does not support the permission to apply for the new version
                 */
                permission.add(PermissionName.PROCESS_OUTGOING_CALLS);
            }
        }

        if (permission.contains(PermissionName.ACTIVITY_RECOGNITION)) {
            if (!isAndroid10() && !permission.contains(PermissionName.BODY_SENSORS)) {
                /*
                 * Automatically add sensor permissions, because this permission is stripped from the
                 * sensor permissions into independent permissions starting from Android 10
                 */
                permission.add(PermissionName.BODY_SENSORS);
            }
        }
    }

    /**
     * Check whether the permission is registered in the manifest file
     *
     * @param requestPermissions requested permission group
     */
    public void checkPermissionManifest(List<String> requestPermissions) {
        List<String> manifestPermissions = getListOfAddedPermissionInManifest();
        if (manifestPermissions == null || manifestPermissions.isEmpty()) {
            throw new ManifestException();
        }

        int minSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minSdkVersion = activity.getApplicationInfo().minSdkVersion;
        } else {
            minSdkVersion = Build.VERSION_CODES.M;
        }

        for (String permission : requestPermissions) {

            if (minSdkVersion < Build.VERSION_CODES.R) {
                if (PermissionName.MANAGE_EXTERNAL_STORAGE.equals(permission)) {
                    if (!manifestPermissions.contains(PermissionName.READ_EXTERNAL_STORAGE)) {
                        /*
                         * In order to ensure normal operation on the old version of the system, this
                         * permission must be registered in the manifest file
                         */
                        throw new ManifestException(PermissionName.READ_EXTERNAL_STORAGE);
                    }

                    if (!manifestPermissions.contains(PermissionName.WRITE_EXTERNAL_STORAGE)) {
                        /*
                         * In order to ensure normal operation on the old version of the system, this
                         * permission must be registered in the manifest file
                         */
                        throw new ManifestException(PermissionName.WRITE_EXTERNAL_STORAGE);
                    }
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.Q) {
                if (PermissionName.ACTIVITY_RECOGNITION.equals(permission)) {
                    if (!manifestPermissions.contains(PermissionName.BODY_SENSORS)) {
                        /*
                         * In order to ensure normal operation on the old version of the system, this
                         * permission must be registered in the manifest file
                         */
                        throw new ManifestException(PermissionName.BODY_SENSORS);
                    }
                }
            }

            if (minSdkVersion < Build.VERSION_CODES.O) {
                if (PermissionName.ANSWER_PHONE_CALLS.equals(permission)) {
                    if (!manifestPermissions.contains(PermissionName.PROCESS_OUTGOING_CALLS)) {
                        /*
                         * In order to ensure normal operation on the old version of the system, this
                         * permission must be registered in the manifest file
                         */
                        throw new ManifestException(PermissionName.PROCESS_OUTGOING_CALLS);
                    }
                }

                if (PermissionName.READ_PHONE_NUMBERS.equals(permission)) {
                    if (!manifestPermissions.contains(PermissionName.READ_PHONE_STATE)) {
                        /*
                         * In order to ensure normal operation on the old version of the system, this
                         * permission must be registered in the manifest file
                         */
                        throw new ManifestException(PermissionName.READ_PHONE_STATE);
                    }
                }
            }

            if (PermissionName.NOTIFICATION_SERVICE.equals(permission)) {
                /*
                 * Do not check whether the notification bar permission is registered in the manifest
                 * file, because this permission is virtual, it does not matter whether it is registered
                 * in the manifest file or not
                 */
                continue;
            }

            if (!manifestPermissions.contains(permission)) {
                throw new ManifestException(permission);
            }
        }
    }
}
