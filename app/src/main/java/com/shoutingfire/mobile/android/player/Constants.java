/**
 * Defines all app and music stream specific values.
 *
 * Hopefully simplifies building multiple player apps
 * from the same, diffable source code trees.
 * Addiions:
 * - Edit each source file and update the package statement.
 * - Edit AndroidManifest.xml and update package name.
 *
 * Copyright (c) 2018 bmir.org and shoutingfire.com
 */
package com.shoutingfire.mobile.android.player;

public class Constants {

    // This app.
    public static final String PACKAGE_NAME = "com.shoutingfire.mobile.android.player";
    public static final String APP_NAME_LOWER = "shoutingfire";
    public static final String APP_NAME_MIXED = "ShoutingFire";
    public static final String APP_NAME_UPPER = "SHOUTINGFIRE";

    // Target stream.
    public static final String MEDIA_HOSTNAME = "shoutingfire-ice.streamguys1.com";
    public static final String MEDIA_URL_STRING = "http://" + MEDIA_HOSTNAME + ":80/live";
    public static final String STATUS_URL_STRING = "http://" + MEDIA_HOSTNAME + "/";

    // Image references.
    public static final int IMG_ICON = R.drawable.shoutingfireicon;
    public static final int IMG_DOTS = R.drawable.shoutingfiredots;
    public static final int IMG_PLAY = R.drawable.shoutingfireplay;
    public static final int IMG_STOP = R.drawable.shoutingfirestop;

    // Notifications.
    ////public static final int NOTIFICATION_ID = 947392519; // BMIR
    public static final int NOTIFICATION_ID = 136342145; // ShoutingFire
}
