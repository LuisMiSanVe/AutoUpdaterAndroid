package com.luismisanve.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    // Server Connection
    private static String URL_FTP = "192.168.1.141"; // Change to your public IP
    private static int FTP_PORT = 21;
    private static String FTP_USERNAME = "user";
    private static String FTP_PASSWORD = "user";

    Button BtnHttp;
    TextView txtStatus;
    static String newVersion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the Design items to the code
        txtStatus = findViewById(R.id.Status);
        TextView txtVersion = findViewById(R.id.TxtVersion);
        BtnHttp = findViewById(R.id.BtnHTTP);

        try {
            txtVersion.setText(getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Checks updates at starting
        new Thread(new Runnable() {
            @Override
            public void run() {
                getUpdate();
            }
        }).start();

        BtnHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getUpdate();
                    }
                }).start();
            }
        });
    }

   public static int compareVersions(String version1, String version2) {
		String[] parts1 = version1.split("\\.");
		String[] parts2 = version2.split("\\.");

		int maxLength = Math.max(parts1.length, parts2.length);

		for (int i = 0; i < maxLength; i++) {
			int v1 = (i < parts1.length) ? Integer.parseInt(parts1[i]) : 0;
			int v2 = (i < parts2.length) ? Integer.parseInt(parts2[i]) : 0;

			if (v1 != v2) {
				return v1 - v2;
			}
		}
		return 0;
	}

    // Update it using FTP
    private void getUpdate() {
        FTPClient ftpClient = new FTPClient();
        try {
            // Connect to the FTP server
            ftpClient.connect(URL_FTP, FTP_PORT);
            boolean login = ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

            if (login) {
                // Set file type to binary (you can change this depending on the file type)
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

                // Retrieve the input stream of the remote file
                InputStream inputStream = ftpClient.retrieveFileStream("version.ver");
                if (inputStream != null) {
                    // Read the file content using BufferedReader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    // Read file line by line and print it (or process it as needed)
                    while ((line = reader.readLine()) != null) {
                        newVersion = line;
                    }

                    // Close the reader and input stream
                    reader.close();
                    inputStream.close();

                    // Complete the file transfer
                    ftpClient.completePendingCommand();
                }

                // Logout from the server
                ftpClient.logout();

                final String version = newVersion.trim();
                // Comparamos las versiones
                if (compareVersions(getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName, version) < 0) {
                    runOnUiThread(() -> {
                        txtStatus.setText("There's a new version (" + version + ")");
                        txtStatus.setTextColor(Color.GREEN);
                        Toast.makeText(MainActivity.this, "Thre's a new version (" + version + ")", Toast.LENGTH_LONG).show();

                        FTPClient ftp = new FTPClient();

                        new AlertDialog.Builder(this)
                                .setTitle("Update avaliable (" + version + ")")
                                .setMessage("A new update has been detected, you wish to install it?")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                                    new Thread(() -> {
                                        // Downloads the APK from the server
                                        try {
                                            ftp.connect(URL_FTP, FTP_PORT);
                                            boolean loginUpd = ftp.login(FTP_USERNAME, FTP_PASSWORD);
                                            if (!loginUpd) {
                                                throw new Exception("FTP Server login failed");
                                            }

                                            ftp.enterLocalPassiveMode();
                                            ftp.setFileType(FTP.BINARY_FILE_TYPE);
                                            String apkFilePath = "testapp_standalone.apk"; // APK path on the FTP server
                                            InputStream is = ftp.retrieveFileStream(apkFilePath);
                                            if (is == null) {
                                                throw new FileNotFoundException("APK file not found on FTP server");
                                            }
                                            ftp.completePendingCommand();

                                            OutputStream os = new FileOutputStream(new File(getExternalFilesDir(null), "testapp_standalone.apk"));

                                            runOnUiThread(() -> {
                                                txtStatus.setTextColor(Color.BLUE);
                                                txtStatus.setText("Downloading Update");
                                                txtStatus.setTextSize((float) 15);
                                                Toast.makeText(this, "Downloading Update...", Toast.LENGTH_LONG).show();
                                            });

                                            byte[] buffer = new byte[4096];
                                            int bytesRead;
                                            while ((bytesRead = is.read(buffer)) != -1) {
                                            os.write(buffer, 0, bytesRead);
                                            }
                                            os.flush();
                                            os.close();

                                            if (ftp.isConnected()) {
                                                ftp.logout();
                                                ftp.disconnect();
                                            }

                                            boolean valid = false;

    										try {
        										new ZipFile(new File(getExternalFilesDir(null), "updater.apk"));
        										// If the ZipFile builder fails...
        										valid = true;
    										} catch (IOException ex){
    										    valid = false;
    										}
    										if (valid) {
                                                // Intents to install Updater
                                                Intent intentInstall = new Intent(Intent.ACTION_VIEW);
                                                Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(getExternalFilesDir(null), "testapp_standalone.apk"));
                                                intentInstall.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                intentInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                startActivity(intentInstall);
    
                                                runOnUiThread(() -> {
                                                    txtStatus.setText("Confirm to install.");
                                                    txtStatus.setTextColor(Color.GREEN);
                                                    txtStatus.setTextSize((float) 15);
                                                });
                                            } else {
                                                runOnUiThread(() -> {
                                                    txtStatus.setText("The download went wrong, try again.");
                                                    txtStatus.setTextColor(Color.RED);
                                                    txtStatus.setTextSize((float) 15);
                                                    BtnHttp.performClick();
                                                });
                                            }
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                            runOnUiThread(() -> {
                                                txtStatus.setTextColor(Color.RED);
                                                txtStatus.setText("Update download failed");
                                                txtStatus.setTextSize((float) 15);
                                                Toast.makeText(this, "Updater download failed", Toast.LENGTH_LONG).show();
                                            });
                                        } catch (Exception ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }).start();
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .show();
                    });
                } else {
                    runOnUiThread(() -> {
                        txtStatus.setTextColor(Color.LTGRAY);
                        Toast.makeText(this, "You already have the last version", Toast.LENGTH_SHORT).show();
                        txtStatus.setText("No updates avaliable");
                        txtStatus.setTextSize((float) 15);
                    });
                }
            } else {
                runOnUiThread(() -> {
                    txtStatus.setTextColor(Color.RED);
                    Toast.makeText(this, "Server login failed", Toast.LENGTH_SHORT).show();
                    txtStatus.setText("Server login failed");
                    txtStatus.setTextSize((float) 15);
                });
            }
        } catch (Exception e) {
            runOnUiThread(() -> {
                txtStatus.setTextColor(Color.RED);
                txtStatus.setText("Checking updates failed");
                txtStatus.setTextSize((float) 15);
                Toast.makeText(this, "Checking updates failed", Toast.LENGTH_LONG).show();
            });
        }
    }
}
