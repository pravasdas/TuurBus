package oditek.com.tuurbus.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class ApiClient {

    public static String host_url = "http://10.25.25.100/tourbusweb/Api/";
    public static String signup = host_url + "login/signup/?appKey=tuurbusoditek";
    public static String signin = host_url + "signup/?appKey=tuurbusoditek";
    public static String myprofile= host_url +"login/profile/?appKey=tuurbusoditek";
    public static String updateprofile= host_url +"login/updateprofile/?appKey=tuurbusoditek";
    public static String filtersearch_url= host_url +"tours/filter?appKey=tuurbusoditek";
    public static String mywishlist_url= host_url +"login/mywishlist/?appKey=tuurbusoditek";
    public static String remove_wishlist_url= host_url +"login/removemywishlist/?appKey=tuurbusoditek";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    public static void saveDataWithKeyAndValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences("turrbus", context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDataFromKey(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("turrbus", context.MODE_PRIVATE);
        String restoredText = prefs.getString(key, "0");
        return restoredText;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
