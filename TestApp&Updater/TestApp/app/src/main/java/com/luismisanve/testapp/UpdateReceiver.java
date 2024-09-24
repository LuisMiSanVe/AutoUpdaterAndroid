package com.luismisanve.testapp;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.File;

public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context con, Intent intent) {
        // If the updated package is our app...
        if (intent.getData().getSchemeSpecificPart().equals("com.luismisanve.updater")) {
            // Intents to start it
            Intent intentUpd = con.getPackageManager().getLaunchIntentForPackage("com.luismisanve.updater");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentUpd.putExtra("Version", MainActivity.newVersion);
            con.startActivity(intentUpd);

            // (OPTIONAL) Deletes cache and all user data
            for (File f : con.getExternalFilesDir(null).listFiles()) {
                f.delete();
            }
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT)
                ((ActivityManager) con.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
        }
    }
}
