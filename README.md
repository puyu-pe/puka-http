# Puka

Servicio de impresión para puntos de venta, utilizando http y comandos EscPOS.

## Indice

1. [Para desarrolladores](#para-desarrolladores)
   1. [Estructura del proyecto](#estructura-del-proyecto) 
   2. [Construir el proyecto](#construir-el-proyecto)
2. [Generación de instaladores](#generación-de-instaladores)
3. Uso


## Para desarrolladores

### Estructura del proyecto

- PukaHTTP
  - installers (Scripts de generación de instaladores de forma manual)
  - src (Código fuente)
    - main
      - java
        - pe.puyu.pukahttp
          - app (Clase Aplicacion JavaFX)
          - model (JavaFX beans)
          - services (Servicios en puka)
            - printer (Servicio de impresión)
            - printingtest (Servicio de pruebas de impresión)
            - api (Api http)
          - util (helpers)
          - validations (Validador parametros de conexión a bifrost)
          - views (Controladores JavaFX)
          - AppLauncher.java (Clase principal)
      - resources
        - pe.puyu.pukahttp
          - assets (Iconos)
          - fonts (Fuentes de texto)
          - styles (Estilos css javafx)
          - testPrinter (Modelos json pruebas de impresión)
          - views (Vistas FXML JavaFX)
  - .env.example (Ejemplo de configuración .env)
  - pom.xml (Declaración de dependencias y plugins compilación)
  - package.json (Configuración jDeploy package)
  - update-package.sh (Script de actualización version package version)

### Construir el proyecto

* Prerequisitos:
  * [JAVA openjdk 17 o superior](https://ed.team/blog/instalar-openjdk-en-linux).
  * [Apache Maven](https://ubunlog.com/apache-maven-instalacion-ubuntu/) , algunos IDE's ya tren maven incluido, ejm.
    Intellij IDEA. (Aunque se recomienda su instalación independiente del IDE)

1. Clonar el repositorio<br>
   Utilizando su IDE favorito o por medio de linea de comandos.
   ```
   git clone git@github.com:puyu-pe/puka-http.git
   ```

2. Ejecutar proyecto<br>
   Utilando su IDE favorito debe ejecutar la acciones maven clean
   compile y javafx:run, o por medio de linea de comandos:
   ```
   mvn clean compile javafx:run
   ```

## Generación de instaladores

Existe dos formas en la que podemos generar los instaladores para windows, mac y distribuciones linux.
Siendo la mas sencilla y recomedada JDeploy, por que automatiza de mejor forma la generación de instaladores,
y trae integrado un mecanismo de actualización automatica para las aplicaciones y tambien integración con github actions.
Sin embargo por razones historicas tambien existe JPackage, este modo con el tiempo quedara deprecado debido a que se esta
migrando toda la generación de instaladores a JDeploy.

1. [Generar instaladores con JDeploy](docs/jdeploy.md)
2. [Generar instaladores con JPackage](docs/jpackage.md)




