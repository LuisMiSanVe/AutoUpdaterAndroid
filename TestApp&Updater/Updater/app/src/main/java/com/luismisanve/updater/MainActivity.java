package com.luismisanve.updater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class MainActivity extends AppCompatActivity {
    private UpdateReceiver packageUpdateReceiver;
    // Server Connection
    private static String URL_FTP = "192.168.1.141"; // Change to your public IP
    private static int FTP_PORT = 21;
    private static String FTP_USERNAME = "user";
    private static String FTP_PASSWORD = "user";

    Button BtnHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the package update trigger
        packageUpdateReceiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        registerReceiver(packageUpdateReceiver, filter);

        // Link the Design items to the code
        TextView txtStatusUpgrade = findViewById(R.id.Status);
        TextView txtVersionAnterior = findViewById(R.id.TxtAnterior);
        TextView txtVersionNueva = findViewById(R.id.TxtNueva);
        TextView txtMensaje = findViewById(R.id.TxtMensaje);
        BtnHttp = findViewById(R.id.BtnHTTP);

        // If it gets no Extra data on the intent (it has not been launched from another app), it doesnt start any update process.
        TextView[] ChangeTexts = {txtStatusUpgrade, txtVersionAnterior};
        Intent receive = getIntent();
        String newVersion = receive.getStringExtra("Version");
        if (newVersion!=null) {
            txtVersionNueva.setText("New version: " + newVersion);

            // Checks updates at starting
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getUpdate(ChangeTexts);
                }
            }).start();

            BtnHttp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getUpdate(ChangeTexts);
                        }
                    }).start();
                }
            });
        } else {
            txtMensaje.setText("");
            txtStatusUpgrade.setText("If you want to update, the updating process must start \nfrom another application");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the app is destroyed
        unregisterReceiver(packageUpdateReceiver);
    }

    // Update it using FTP
    private void getUpdate(TextView[] textosActualizar) {
        FTPClient ftpClient = new FTPClient();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            // Get the version of our app
            String currentVersion = "";
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo("com.luismisanve.testapp", 0);
                currentVersion = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            final String FCurrent = currentVersion;
            runOnUiThread(() -> {
                textosActualizar[1].setText("Current version: " + FCurrent);
            });


            try {
                FTPClient ftp = new FTPClient();

                ftp .connect(URL_FTP, FTP_PORT);
                boolean login = ftp .login(FTP_USERNAME, FTP_PASSWORD);
                if (!login ) {
                    throw new Exception("The Login on the FTP Server failed");
                }

                ftp .enterLocalPassiveMode();
                ftp .setFileType(FTP.BINARY_FILE_TYPE);
                String apkFilePath = "testapp.apk"; // APK path on the FTP server
                InputStream is = ftp .retrieveFileStream(apkFilePath);
                if (is == null) {
                    throw new FileNotFoundException("APK file not found on FTP server");
                }

                OutputStream os = new FileOutputStream(new File(getExternalFilesDir(null), "testapp.apk"));

                runOnUiThread(() -> {
                    BtnHttp.setEnabled(false);
                    textosActualizar[0].setText("Downloading APK...");
                    textosActualizar[0].setTextColor(Color.BLUE);
                    Toast.makeText(MainActivity.this, "Downloading APK...", Toast.LENGTH_LONG).show();
                });

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
                os.close();

                // Closes the connection
                if (ftp.isConnected()) {
                    ftp .logout();
                    ftp .disconnect();
                }

                // Intent to install the app
                Intent intentInstall = new Intent(Intent.ACTION_VIEW);
                Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(getExternalFilesDir(null), "testapp.apk"));
                intentInstall.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intentInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intentInstall);

                runOnUiThread(() -> {
                    BtnHttp.setEnabled(true);
                    textosActualizar[0].setText("Confirm to install.");
                    textosActualizar[0].setTextColor(Color.GREEN);
                });

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Closes the connection
                if (ftp .isConnected()) {
                    ftp .logout();
                    ftp .disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "The download failed", Toast.LENGTH_LONG).show());
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                textosActualizar[0].setText("Error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                textosActualizar[0].setTextColor(Color.RED);
                Toast.makeText(MainActivity.this, "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
