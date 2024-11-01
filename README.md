> [Ver en ingles/See in english](https://github.com/LuisMiSanVe/AutoUpdaterAndroid/tree/main)
# 📱 Actualizador Automático de Aplicaciones Android
Dos proyectos, 'SelfUpdater' (AutoActualizador) y 'TestApp&Updater' (AppPruebaYActualizador), uno recoge una APK con la actualización y la instala de la forma más simple posible, mientras que la otra usa una aplicación auxiliar para el mismo proceso.
Deberás implementar el codigo de la TestApp en su aplicación para lanzar el Updater correctamente.
## 📋 Prerequisitos
La aplicación Updater recoge de un Servidor FTP la APK por lo que necesitarás leventar un servidor por ejemplo uno local con [XAMPP](https://www.apachefriends.org/es/index.html) o programas similares.
> [!NOTE]
> Usaré XAMPP para explicar la funcionalidad del proyecto

## 🛠️ Instalación
En XAMPP, levanta el Servidor FirezillaFTP, dale clic a 'Admin' y conectate al Servidor.\
Arriba, selecciona `Edit>Users` y añade un nuevo usuario llamado 'user' (puedes llamarlo como quieras pero tendrás que cambiarlo en el código, pues así esta puesto por defecto) habilita la contraseña y escribe 'user' como contraseña tambien.\
En la sección Page, selecciona 'Shared Folders' y añade la carpeta de 'FTPServer' que clonaste del repositorio y dale clic a 'Set as home dir'.
> [!NOTE]
> Dentro de 'FTPServer', 'updater.apk' solo se usa en el proyecto de TestApp&Updater.

En el código, cambia 'URL_FTP' a tu IP publica (Comando `ipconfig` en Windows).\
Con eso, el Servidor FTP está configurado, ahora deberías implementar el código de TestApp a tu aplicación.
## 📖 Sobre la App
Dependiendo de tus necesidades, el Updater es opcional, ya que está pensado de que en caso de actualizar necesites <b>borrar todos los datos de la aplicación</b>. A veces, datos de versiones anteriores pueden dar problemas con las nuevas, pero por como Android está hecho, ese proceso no se puede hacer todo en una misma app (borrar todos los datos y luego actualizarse).
En caso que no necesites borrar tus datos al actualzar, usa la aplicacion self-update.
- TestApp y Updater:
Al inciar (o al darle al botón) busca por FTP el fichero 'version.ver' para comparar si la versión publicada es mayor o no a la instalada. Si el archivo marca una versión más nueva, el <b>proceso de actualización</b> comienza.
La testapp intenta llamar a Updater en caso que esté instalada, si no lo está, descarga la APK del Servidor FTP y lo instala.
El Updater se abre solo en cuanto es instalado y empieza a descargar la APK con la actualización.
- SelfUpdater:
Mismo proceso que la anterior pero no borra ningun dato y no instala ninguna app auxiliar.
## 📂 Archivos
En el resitorio hay dos carpetas principales:
- TestApp&Updater: Incluye dos apps, una de la que tienes que implementar el código en la tuya propia (TestApp) y la auxiliar (Updater), principalmente usada para cuando quieres borrar todos los datos antes de actualizar.
- SelfUpdater: solo una app, necesitas incluir el código en tu propia aplicación.

Los proyectos tienen una clase llamada 'UpdateReciever.cs' que se ejecuta automaticamente cuando un paquete se actualiza.
## 💻 Tecnologías usadas
- Lenguaje de programación: Java
- Plantilla: Empty Views Activity
- API de Android: 24
- Librerias:
  - FileProvider
  - FTP (3.8.0)
- Otras:
  - XAMPP (3.3.0)
    - FirezillaFTP
  - [FreeIcons](https://freeicons.io/) (Imagen)
- IDE Recomendado: Android Studio (Koala Feature Drop 2024.1.2)
