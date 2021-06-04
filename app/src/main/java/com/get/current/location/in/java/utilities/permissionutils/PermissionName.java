package com.get.current.location.in.java.utilities.permissionutils;

public class PermissionName {

    private PermissionName() {
        throw new UnsupportedOperationException("Should not create instance of PermissionName class. Please use as static..");
    }

    /**
     * External storage permission (special permission, Android 11 and above required)
     */
    public static final String MANAGE_EXTERNAL_STORAGE = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;

    /**
     * Get activity steps (requires Android 10.0 and above)
     */
    public static final String ACTIVITY_RECOGNITION = android.Manifest.permission.ACTIVITY_RECOGNITION;

    /**
     * Allow the calling application to continue the call initiated in another application (Android 9.0 and above are required, which is extremely rare, so no adaptation is required)
     */
    public static final String ACCEPT_HANDOVER = android.Manifest.permission.ACCEPT_HANDOVER;

    /**
     * Install application permissions (special permissions, Android 8.0 and above required)
     */
    public static final String REQUEST_INSTALL_PACKAGES = android.Manifest.permission.REQUEST_INSTALL_PACKAGES;

    /**
     * Notification bar permission (special permission, Android 6.0 and above required)
     */
    public static final String NOTIFICATION_SERVICE = android.Manifest.permission.ACCESS_NOTIFICATION_POLICY;
    /**
     * Floating window permissions (special permissions, Android 6.0 and above required)
     */
    public static final String SYSTEM_ALERT_WINDOW = android.Manifest.permission.SYSTEM_ALERT_WINDOW;
    /**
     * System setting permissions (special permissions, Android 6.0 and above required)
     */
    public static final String WRITE_SETTINGS = android.Manifest.permission.WRITE_SETTINGS;

    public static final String READ_CALENDAR = android.Manifest.permission.READ_CALENDAR;
    public static final String WRITE_CALENDAR = android.Manifest.permission.WRITE_CALENDAR;

    public static final String CAMERA = android.Manifest.permission.CAMERA;

    public static final String READ_CONTACTS = android.Manifest.permission.READ_CONTACTS;
    public static final String WRITE_CONTACTS = android.Manifest.permission.WRITE_CONTACTS;
    public static final String GET_ACCOUNTS = android.Manifest.permission.GET_ACCOUNTS;

    public static final String ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    /**
     * Get location in the background (requires Android 10.0 and above)
     */
    public static final String ACCESS_BACKGROUND_LOCATION = android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
    /**
     * Read the geographic location in the photo (requires Android 10.0 and above)
     */
    public static final String ACCESS_MEDIA_LOCATION = android.Manifest.permission.ACCESS_MEDIA_LOCATION;

    public static final String RECORD_AUDIO = android.Manifest.permission.RECORD_AUDIO;

    public static final String READ_PHONE_STATE = android.Manifest.permission.READ_PHONE_STATE;
    public static final String CALL_PHONE = android.Manifest.permission.CALL_PHONE;
    public static final String READ_CALL_LOG = android.Manifest.permission.READ_CALL_LOG;
    public static final String WRITE_CALL_LOG = android.Manifest.permission.WRITE_CALL_LOG;
    public static final String ADD_VOICEMAIL = android.Manifest.permission.ADD_VOICEMAIL;
    public static final String USE_SIP = android.Manifest.permission.USE_SIP;
    /**
     * Handling outgoing calls
     *
     * @deprecated is deprecated in Android 10, please use {@link PermissionName#ANSWER_PHONE_CALLS} directly
     */
    public static final String PROCESS_OUTGOING_CALLS = android.Manifest.permission.PROCESS_OUTGOING_CALLS;
    /**
     * Answer the call (Android 8.0 and above required)
     */
    public static final String ANSWER_PHONE_CALLS = android.Manifest.permission.ANSWER_PHONE_CALLS;
    /**
     * Read mobile phone number (Requires Android 8.0 and above)
     */
    public static final String READ_PHONE_NUMBERS = android.Manifest.permission.READ_PHONE_NUMBERS;

    public static final String BODY_SENSORS = android.Manifest.permission.BODY_SENSORS;

    public static final String SEND_SMS = android.Manifest.permission.SEND_SMS;
    public static final String RECEIVE_SMS = android.Manifest.permission.RECEIVE_SMS;
    public static final String READ_SMS = android.Manifest.permission.READ_SMS;
    public static final String RECEIVE_WAP_PUSH = android.Manifest.permission.RECEIVE_WAP_PUSH;
    public static final String RECEIVE_MMS = android.Manifest.permission.RECEIVE_MMS;

    /**
     * Read external storage
     *
     * @deprecated is deprecated in Android 11, please use {@link PermissionName#MANAGE_EXTERNAL_STORAGE} directly
     */
    @Deprecated
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /**
     * Write to external storage
     *
     * @deprecated is deprecated in Android 11, please use {@link PermissionName#MANAGE_EXTERNAL_STORAGE} directly
     */
    @Deprecated
    public static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final class Group {

        public static final String[] CALENDAR = new String[]{
                PermissionName.READ_CALENDAR,
                PermissionName.WRITE_CALENDAR};

        public static final String[] CAMERA = new String[]{
                PermissionName.CAMERA};

        public static final String[] CONTACTS = new String[]{
                PermissionName.READ_CONTACTS,
                PermissionName.WRITE_CONTACTS,
                PermissionName.GET_ACCOUNTS};

        public static final String[] LOCATION = new String[]{
                PermissionName.ACCESS_FINE_LOCATION,
                PermissionName.ACCESS_COARSE_LOCATION,
                PermissionName.ACCESS_BACKGROUND_LOCATION};

        public static final String[] MICROPHONE = new String[]{
                        PermissionName.RECORD_AUDIO};

        public static final String[] PHONE = new String[]{
                PermissionName.READ_PHONE_STATE,
                PermissionName.CALL_PHONE,
                PermissionName.READ_CALL_LOG,
                PermissionName.WRITE_CALL_LOG,
                PermissionName.ADD_VOICEMAIL,
                PermissionName.USE_SIP,
                PermissionName.PROCESS_OUTGOING_CALLS};


        public static final String[] SENSORS = new String[]{
                PermissionName.BODY_SENSORS,
                PermissionName.ACTIVITY_RECOGNITION};

        public static final String[] SMS = new String[]{
                PermissionName.SEND_SMS,
                PermissionName.RECEIVE_SMS,
                PermissionName.READ_SMS,
                PermissionName.RECEIVE_WAP_PUSH,
                PermissionName.RECEIVE_MMS};

        /**
         * Storage permissions
         *
         * @deprecated is deprecated in Android 11, please use {@link PermissionName#MANAGE_EXTERNAL_STORAGE}
         */
        @Deprecated
        public static final String[] STORAGE = new String[]{
                PermissionName.READ_EXTERNAL_STORAGE,
                PermissionName.WRITE_EXTERNAL_STORAGE};
    }
}