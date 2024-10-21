# 📱 Automatic Android app Updater
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
> The file inside 'FTPServer', 'updater.apk' is only used on the TestApp&Updater Project.

On the code, change 'URL_FTP' to your public IP (`ipconfig` command on Windows).\
With that, the FTP Server is configured, now you should just implement the TestApp's code to your app.
## 📖 About the app
Depending of your needs the Updater can be optional, because it's mean is to install updates to an app in case you need to <b>delete all the app data before updating</b>. Sometimes, data from older versions can mess up with the new ones, but because of how Android is made, you can't do all that just from one app (delete all data then install update).
In case you don't need to delete the data of your app at updating, just use the self-updating app.
- TestApp & Updater:
When started (or when clicking a button) it searches on the FTP for the 'version.ver' file and compare if the current version is lower or not to the one written on the file. If the file has a newer version, the <b>Update Process</b> begins.
The testapp tries to call Updater in case is already installed, if it isn't, it downloads the APK of your FTP Server and install it.
The Updater opens instantly as it's installed and starts downloading the updated app APK.
- SelfUpdater:
Same process of the previous one but it doesn't delete any user data and it doesn't install any auxiliar app.
## 📂 Files
On the repository are two main folders:
- TestApp&Updater: it includes two apps, the one you need to implement the code from (TestApp) and the auxiliary one (Updater), mainly used if you want to delete data of your app before updating.
- SelfUpdater: just one app, you need to include the code of it in your app.

The TestApp&Updater projects have a class called 'UpdateReceiver.cs' that instantly triggers when a package is updated.
## 💻 Techonogies used
- Programming Lenguage: Java
- Template: Empty Views Activity
- Android API: 24
- Libraries:
  - FileProvider
  - FTP (3.8.0)
- Other:
  - XAMPP (3.3.0)
    - FirezillaFTP
  - [FreeIcons](https://freeicons.io/) (Image)
- Recommended IDE: Android Studio (Koala Feature Drop 2024.1.2)
