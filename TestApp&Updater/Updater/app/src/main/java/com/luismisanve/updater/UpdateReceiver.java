package com.luismisanve.updater;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.*;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context con, Intent intent) {
        // If the updated package is our app...
        if (intent.getData().getSchemeSpecificPart().equals("com.luismisanve.testapp")) {
            // Intents to start it
            Intent intentAdv = con.getPackageManager().getLaunchIntentForPackage("com.luismisanve.testapp");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            con.startActivity(intentAdv);

            // (OPTIONAL) Deletes cache and all user data
            for (File f : con.getExternalFilesDir(null).listFiles()) {
                f.delete();
            }
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT)
                ((ActivityManager) con.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        }
    }
}
