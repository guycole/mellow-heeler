package net.braingang.heeler;

import android.app.PendingIntent;

import com.google.android.gms.location.LocationResult;

public class Personality {

    /**
     *
     */
    public static LocationResult locationResult;
    public static PendingIntent geoLocPending;
    // true services should exit
    public static boolean gracefulExit = false;

    /*
    public static String AwsAccessKey = "bogus";
    public static String AwsSecretKey = "bogus";
    public static String AwsRegion = "us-east-1";
    public static String AwsBucketName = "bogus";
    */

    public static String AwsAccessKey = "";
    public static String AwsSecretKey = "";
    public static String AwsRegion = "us-east-1";
    public static String AwsBucketName = "s3://mellow-heeler.braingang.net/android1";
}
