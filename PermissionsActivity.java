package com.example.mypermissionsapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

/**
 * Created by stanimir on 21.06.16.
 */
public class PermissionsActivity extends AppCompatActivity {

    private static final String TAG = PermissionsActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 975;

    /**
     * Universal method for requesting runtime permissions.
     *
     * @param activity    The activity from where you are calling this method.
     *                    Usually is used "this" or if you are in fragment "getActivity()".
     * @param permissions String array of the requested permissions. For example "String[] permissionsArray =
     *                    {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};"
     *                    and pass to this method "permissionsArray".
     * @return TRUE or FALSE if the permissions are granted or not.
     */
    public boolean hasPermissions(Activity activity, String[] permissions) {
        boolean hasPermission = true;

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                Log.d(TAG, permission + " permissions has NOT been granted. It will be requested.");
            } else {
                Log.d(TAG, permission + " permissions has already been granted.");
            }
        }

        if (hasPermission) {
            return true;
        } else {
            requestPermission(activity, permissions);
            return false;
        }
    }

    private void requestPermission(final Activity activity, final String[] requestedPermissions) {
        boolean shouldRequestRational = false;

        for (String permission : requestedPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                shouldRequestRational = true;
            }
        }

        if (shouldRequestRational) {
            Log.d(TAG, "Displaying " + Arrays.toString(requestedPermissions) +
                       " permission rationale to provide additional context.");
            Snackbar.make(findViewById(android.R.id.content),
                getProperPermissionsGroup(requestedPermissions) + " permissions are needed.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(activity, requestedPermissions,
                            REQUEST_PERMISSIONS);
                    }
                })
                .show();
        } else {
            ActivityCompat.requestPermissions(activity, requestedPermissions, REQUEST_PERMISSIONS);
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String getProperPermissionsGroup(String[] permissions) {
        String msg = "";

        switch (permissions[0]) {
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                msg = "Calendar";
                break;
            case Manifest.permission.CAMERA:
                msg = "Camera";
                break;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                msg = "Contacts";
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                msg = "Location";
                break;
            case Manifest.permission.RECORD_AUDIO:
                msg = "Microphone";
                break;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.ADD_VOICEMAIL:
            case Manifest.permission.USE_SIP:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                msg = "Phone";
                break;
            case Manifest.permission.BODY_SENSORS:
                msg = "Sensors";
                break;
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
            case Manifest.permission.RECEIVE_MMS:
                msg = "SMS";
                break;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                msg = "Storage";
                break;
        }
        return msg;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                Log.d(TAG, "Received response for " + Arrays.toString(permissions) +
                           " permissions request.");
                if (verifyPermissions(grantResults)) {
                    Log.d(TAG, Arrays.toString(permissions) + " permissions has now been granted.");
                    Snackbar.make(findViewById(android.R.id.content),
                        getProperPermissionsGroup(permissions) + " permissions has been granted",
                        Snackbar.LENGTH_SHORT)
                        .show();
                } else {
                    Log.d(TAG, Arrays.toString(permissions) + " permissions were NOT granted.");
                    Snackbar.make(findViewById(android.R.id.content),
                        getProperPermissionsGroup(permissions) + " permissions were not granted",
                        Snackbar.LENGTH_SHORT)
                        .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
