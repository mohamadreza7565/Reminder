package com.rymo.felfel.common;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.rymo.felfel.BuildConfig;

import java.util.ArrayList;

public class RequestPermission {

    public final static String SMS = "SMS", CAMERA = "CAMERA", STORAGE = "STORAGE", LOCATION = "LOCATION", VOICE_RECORD = "VOICE_RECORD";
    Activity activity;

    public static RequestPermission newInstance(Activity activity) {
        return new RequestPermission(activity);
    }


    public RequestPermission(Activity activity) {
        this.activity = activity;
    }

    public boolean checkPermissionList(String[] permissionTypes) {

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        for (int i = 0; i < permissionTypes.length; i++) {
            switch (permissionTypes[i]) {
                case CAMERA:
                    int permissionACCESS_CAMERA = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.CAMERA);

                    if (permissionACCESS_CAMERA != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.CAMERA);
                    }
                    break;
                case STORAGE:
                    int permissionACCESS_READ_STORAGE = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permissionACCESS_READ_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                    int permissionACCESS_WRITE_STORAGE = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionACCESS_WRITE_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    break;
                case LOCATION:
                    int permissionACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                    int permissionACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (permissionACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                    }
                    break;
                case VOICE_RECORD:
                    int permissionACCESS_RECORD_AUDIO = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.RECORD_AUDIO);
                    if (permissionACCESS_RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
                    }
                    break;

                case SMS:
                    int permissionACCESS_SEND_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.SEND_SMS);
                    if (permissionACCESS_SEND_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
                    }

                    int permissionACCESS_RECEIVE_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.RECEIVE_SMS);
                    if (permissionACCESS_RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
                    }

                    int permissionACCESS_READ_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_SMS);
                    if (permissionACCESS_READ_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.READ_SMS);
                    }
                    break;
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean requestList(String[] permissionTypes) {

        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        for (int i = 0; i < permissionTypes.length; i++) {
            switch (permissionTypes[i]) {
                case CAMERA:
                    int permissionACCESS_CAMERA = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.CAMERA);

                    if (permissionACCESS_CAMERA != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.CAMERA);
                    }
                    break;
                case STORAGE:
                    int permissionACCESS_READ_STORAGE = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permissionACCESS_READ_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                    int permissionACCESS_WRITE_STORAGE = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionACCESS_WRITE_STORAGE != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    break;
                case LOCATION:
                    int permissionACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
                    }

                    int permissionACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_COARSE_LOCATION);
                    if (permissionACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                    }
                    break;
                case VOICE_RECORD:
                    int permissionACCESS_RECORD_AUDIO = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.RECORD_AUDIO);
                    if (permissionACCESS_RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
                    }
                    break;

                case SMS:
                    int permissionACCESS_SEND_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.SEND_SMS);
                    if (permissionACCESS_SEND_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
                    }

                    int permissionACCESS_RECEIVE_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.RECEIVE_SMS);
                    if (permissionACCESS_RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
                    }

                    int permissionACCESS_READ_SMS = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_SMS);
                    if (permissionACCESS_READ_SMS != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.READ_SMS);
                    }
                    break;
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQ_ALL_PERMISSIONS);
            return false;
        }
        return true;
    }

    public boolean checkSmsPermission(Boolean doRequest) {
        ArrayList<String> listPermissionsNeeded = new ArrayList<>();

        int permissionACCESS_SEND_SMS = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.SEND_SMS);
        if (permissionACCESS_SEND_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }

        int permissionACCESS_RECEIVE_SMS = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECEIVE_SMS);
        if (permissionACCESS_RECEIVE_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS);
        }

        int permissionACCESS_READ_SMS = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_SMS);
        if (permissionACCESS_READ_SMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }

        int permissionACCESS_READ_PHONE_STATE = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);

        if (permissionACCESS_READ_PHONE_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            if (doRequest) {
                ActivityCompat.requestPermissions(activity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQ_SMS_PERMISSIONS);
            }
            return false;
        }
        return true;

    }

    public boolean checkStoragePermission(Boolean doRequest) {

        if (SDK_INT >= Build.VERSION_CODES.R) {
            Boolean generated = Environment.isExternalStorageManager();
            if (!generated && doRequest) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                activity.startActivityForResult(intent, Constants.REQ_STORAGE_PERMISSIONS);
            }
            return generated;
        } else {
            ArrayList<String> listPermissionsNeeded = new ArrayList<>();

            int permissionACCESS_READ_STORAGE = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionACCESS_READ_STORAGE != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            int permissionACCESS_WRITE_STORAGE = ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionACCESS_WRITE_STORAGE != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }


            if (!listPermissionsNeeded.isEmpty()) {
                if (doRequest) {
                    ActivityCompat.requestPermissions(activity,
                            listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), Constants.REQ_STORAGE_PERMISSIONS);
                }
                return false;
            }
            return true;
        }
    }

}
