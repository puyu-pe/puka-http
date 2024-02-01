# Generar instaladores con JPackage

En la carpeta installers/ se encuentran los scripts generadores de instaladores,
y se tiene que ejecutar uno de ellos dependiendo del sistema operativo. No hay soporte para compilación
cruzada por lo tanto si se quiere generar un instalador para windows, se tiene que contruir todo en una maquina
con windows.

Los scripts utilizan jpackage , jdeps y jlink.
[Ver el proyecto JPackageScriptFX](https://github.com/dlemmermann/JPackageScriptFX.git)
para mas información.

> NOTA: **JAVA_HOME**, variable de entorno, tiene que estar correctamente configurado.
> ``` 
> $ echo $JAVA_HOME                                                                                                                            ─╯
>/usr/lib/jvm/java-17-openjdk 
> ```

## Tipos de instaladores

* Para windows
  * msi (Requisito [WIX toolset ](https://wixtoolset.org))
  * exe (Requisito [WIX toolset ](https://wixtoolset.org))
  * app-image (No probado en la maquina del autor)

* Para linux
  * deb (Una distribución debian)
  * rpm (Una distribución redhat)
  * app-image (Probado solo en archlinux)

* Para mac
  * dmg
  * pkg

## Pasos previos

1. Generar el empaquetado jar
   ```bash
   mvn clean compile package
   ```

2. Crear archivo .env segun .env.example
   ```
   cp .env.example .env
   ```
3. Configurar propiedades archivo .env

* APP_VERSION: Version de la aplicacion a generar, según el archivo
  VERSION en la raiz del proyecto.
* PROJECT_VERSION: Version del proyecto,
  **OJO: Tiene que ser la misma version que el pom.xml.**

```xml

<project>
  <!-- ... demas configuraciones -->
  <groupId>pe.puyu</groupId>
  <artifactId>PukaFX</artifactId>
  <version>0.3.0</version> <!-- PROJECT_VERSION -->
  <!-- ... demas configuraciones -->
</project>
```

> WARNING: si no se especifica la misma version del pom.xml, fallara el script generador de instaladores

* JAVA_VERSION: Version de java con la que se tiene que generar
  el instalador. (17 o superior).
* INSTALLER_TYPE: Tipo de instalador soportado: msi, exe, app-image, deb, rpm.

## Generar instaladores

### 1. Establecer las variables de entorno

El siguiente comando recorrera cada una de las variables especificadas
en el .env y las establecera para que sean visibles para el script.

* windows:
  ```shell
  Get-Content .env | ForEach-Object {
   if ($_ -match '^(.*?)=(.*)$'){
     Set-Item -Path "Env:\$($Matches[1])" -Value $Matches[2]
   }
  }

* linux:
  ```bash
  export $(cat .env | xargs)
  ```
* mac:<br>
  Modificar manualmente en su script correspondiente installer-mac.sh

#### 2. Ejecutar script, para generar el instaldor

* windows:
  ```bash
  ./installers/installer-windows.bat
  ```
* linux:
  ```bash
  ./installers/installer-linux.sh
  ```
* mac:
  ```bash
  ./installers/installer-mac.sh
  ```

## ¿ Problemas con el empaquetado final del instalador ?

Si la aplicación final generado con el instalador, falla de manera extraña
sin razón alguna, puede deberse a que falta un modulo en el empaquetado final.

Modificar el bloque de codigo jlink --add-modules:

```bash
# ...

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --strip-native-commands \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules "${detected_modules}${manual_modules}" \
  --output target/java-runtime
  
# ...
```

A lo siguiente:

```bash
# ...

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --strip-native-commands \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules ALL-MODULE-PATH \
  --output target/java-runtime
# solo modificar --add-modules 
# ...
```

> Nota: Puede listar todos los modulos disponibles en java
> ``` 
> $ java --list-modules 
> ```
