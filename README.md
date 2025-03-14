> [See in spanish/Ver en español](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/blob/main/README.es.md)
# 📱 Automatic Android app Updater
[![image](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![image](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/studio)
[![image](https://img.shields.io/badge/Xampp-F37623?style=for-the-badge&logo=xampp&logoColor=white)](https://www.apachefriends.org/es/index.html)
[![image](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)](https://developer.android.com/studio)

Two projects, 'SelfUpdater' and 'TestApp&Updater', one that takes the APK file of your update and installs it in the most user-friendly way possible, the other one has an auxiliary app that does the job.
You'll have to implement the code from TestApp to properly launch Updater.
## 📋 Prerequisites
The Updater app takes from a FTP Server the APKs so you'll need to have one or rise your own local server with [XAMPP](https://www.apachefriends.org/es/index.html) or similar programs.
> [!NOTE]
> I'll use XAMPP to explain the app's functionality

## 🛠️ Setup
On XAMPP, rise the FirezillaFTP server, then click on 'Admin' and connect to the server.\
On the top of the window, select `Edit>Users` and add a new user called 'user' (you can name it whatever you like but you'll have to change it on the code, by default is set to 'user') check the password and write 'user' as the password as well.\
In the Page section, now select 'Shared folders' and add the 'FTPServer' folder you cloned from the repository and then click 'Set as home dir'.
> [!NOTE]
> The file inside [FTPServer, 'updater.apk'](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main/FTPServer) is only used on the TestApp&Updater Project.

On the code, change 'URL_FTP' to your public IP (`ipconfig` command on Windows).\
With that, the FTP Server is configured, now you should just implement the TestApp's code to your app.
## 📖 About the app
Depending of your needs the Updater can be optional, because it's mean is to install updates to an app in case you need to <b>delete all the app data before updating</b>. Sometimes, data from older versions can mess up with the new ones, but because of how Android is made, you can't do all that just from one app (delete all data then install update).
In case you don't need to delete the data of your app at updating, just use the self-updating app.
- TestApp & Updater:
When started (or when clicking a button) it searches on the FTP for the [version.ver](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/blob/main/FTPServer/version.ver) file and compare if the current version is lower or not to the one written on the file. If the file has a newer version, the <b>Update Process</b> begins.
The testapp tries to call Updater in case is already installed, if it isn't, it downloads the APK of your FTP Server and install it.
The Updater opens instantly as it's installed and starts downloading the updated app APK.
- SelfUpdater:
Same process of the previous one but it doesn't delete any user data and it doesn't install any auxiliar app.

> [!TIP]
> If you have a slow connection with the FTP Server, the verification it uses in [SelfUpdater (line 158)](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/blob/main/SelfUpdater/app/src/main/java/com/luismisanve/testapp/MainActivity.java) and [TestApp (line 186)](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/blob/main/TestApp%26Updater/TestApp/app/src/main/java/com/luismisanve/testapp/MainActivity.java) may slow down or even freeze the download, resulting in a timeout, so consider commenting that line.

## 📂 Files
On the repository are two main folders:
- [TestApp&Updater](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main/TestApp%26Updater): it includes two apps, the one you need to implement the code from ([TestApp](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main/TestApp%26Updater/TestApp)) and the auxiliary one ([Updater](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main/TestApp%26Updater/Updater)), mainly used if you want to delete data of your app before updating.
- [SelfUpdater](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main/SelfUpdater): just one app, you need to include the code of it in your app.

The TestApp&Updater projects have a class called [UpdateReceiver.cs](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/blob/main/TestApp%26Updater/TestApp/app/src/main/java/com/luismisanve/testapp/UpdateReceiver.java) that instantly triggers when a package is updated.
## 💻 Techonogies used
- Programming Lenguage: [Java](https://www.java.com/)
- Template: Empty Views Activity
- Android API: 24
- Libraries:
  - [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider)
  - FTP (3.8.0)
- Other:
  - [XAMPP](https://www.apachefriends.org/es/index.html) (3.3.0)
    - [FirezillaFTP](https://filezilla-project.org/)
  - [FreeIcons](https://freeicons.io/) (Image)
- Recommended IDE: [Android Studio](https://developer.android.com/studio) (Koala Feature Drop 2024.1.2)
