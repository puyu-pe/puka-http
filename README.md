```textmate
/$$$$$$$  /$$   /$$ /$$   /$$  /$$$$$$          /$$   /$$ /$$$$$$$$ /$$$$$$$$ /$$$$$$$
| $$__  $$| $$  | $$| $$  /$$/ /$$__  $$        | $$  | $$|__  $$__/|__  $$__/| $$__  $$
| $$  \ $$| $$  | $$| $$ /$$/ | $$  \ $$        | $$  | $$   | $$      | $$   | $$  \ $$
| $$$$$$$/| $$  | $$| $$$$$/  | $$$$$$$$ /$$$$$$| $$$$$$$$   | $$      | $$   | $$$$$$$/
| $$____/ | $$  | $$| $$  $$  | $$__  $$|______/| $$__  $$   | $$      | $$   | $$____/
| $$      | $$  | $$| $$\  $$ | $$  | $$        | $$  | $$   | $$      | $$   | $$      
| $$      |  $$$$$$/| $$ \  $$| $$  | $$        | $$  | $$   | $$      | $$   | $$      
|__/       \______/ |__/  \__/|__/  |__/        |__/  |__/   |__/      |__/   |__/

```

Servicio de impresión para la impresion de tickets en impresoras termicas.

# Indice 📖

1. [Instalación y uso](#instalación-)
2. [Para desarrolladores](#para-desarrolladores--)
    * [Comenzando](#comenzando-)
    * [Despliegue](#despliegue-)
3. [Generar instaladores](#generar-instaladores-multiplataforma-)

# Instalación 🔧

* PukaHTTP esta disponible para su descarga en su ultima
  version: [Pagina de descarga](https://www.jdeploy.com/gh/puyu-pe/puka-http).
* Despues de instalar, al abrir PukaHTTP por primera vez, se tendra que configurar la **IP** y el **Puerto** del
  servidor local http opcionalmente se le puede configurar un logo.
  > IMPORTANTE!!: El **IP** tiene que ser el mismo ip que la maquina host (se puede averiguar con el comando **ipconfig
  ** en windows y
  **ifconfig** de net-tools en linux y mac).
  Por defecto Puka tratara de identificar la IP de la maquina host, pero puede no ser tan certero.
* Luego de aceptar la configuración, para maquinas con S.O Windows y Mac se mostrara un TrayIcon en en la bandeja de sistema del
  entorno de escritorio del sistema operativo, para maquinas con S.O Linux u otro se habilitara una ventana como alternativa al trayIcon, esto debido
  a problemas de soporte de trayicon en S.O Linux.

# Api 📖

* Test connection service online: 
  ```
  "/" -> HTTP METHOD: GET
  ```
* Mandar a imprimir un trabajo de impresión:
  ```
  /print -> HTTP METHOD: POST
  ```
  Query Params:
  - printer: Nombre de la impresora o ip si es un impresora tipo ETHERNET
  - type: Por defecto SYSTEM, representa el tipo de impresora: SYSTEM | ETHERNET | SERIAL | SAMBA
  - port: Puerto de impresora en caso sea tipo ETHERNET
  Ejemplo:
  ```
  /print?printer=EPSON_TMIII&type=SYSTEM
  ```
* Obtener elementos en cola, (para impresoras tipo SYSTEM, la cola de impresión lo maneja el propio sistema operativo): 
  ```
  "/print/queue" -> HTTP METHOD: GET
  ```
* Reimprimir elementos en cola, (para impresoras tipo SYSTEM, la cola de impresión lo maneja el propio sistema operativo): 
  ```
  "/print/queue" -> HTTP METHOD: PUT
  ```
* Liberar elementos en cola, (para impresoras tipo SYSTEM, la cola de impresión lo maneja el propio sistema operativo): 
  ```
  "/print/queue" -> HTTP METHOD: DELETE
  ```

# Para desarrolladores ☕ 🍺

## Comenzando 🚀

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

## Despliegue 📦

### Preparar una nueva versión 🛠️

Tener instalado [git-flow ](https://desarrollowp.com/blog/tutoriales/aprende-git-de-manera-sencilla-git-flow/) y [git-flow-hooks](https://github.com/jaspernbrouwer/git-flow-hooks)

1. Ejecutar git flow hotfix o release
   ```bash
   git flow release start
   ```
   ó
   ```bash
   git flow hotfix start
   ```
2. Ejecutar el script update-package.sh
   ```bash
   ./update-package.sh
   ```
   > Nota: El script funciona solo en maquinas Linux y Mac, en windows modificar la versión en package.develop.json y
   package.production.json de forma manual
3. Confirmar los cambios
   ```bash
   git add . && git commit
   ```
4. Finalizar la rama del hotfix o release
   ```bash
   git flow hotfix finish
   ```
   'o'
   ```bash
   git flow release finish
   ```
5. Hacer push -all y a la ultima etiqueta generada
   ```bash
   git push --all && git push origin $(cat VERSION)
   ```
   > Warning: **No** ejecutar **git push --tags**, ya que puede entrar en conflicto con el tag jdeploy

# Generar instaladores multiplataforma 🎁

Existe dos formas en la que podemos generar los instaladores para windows, mac y distribuciones linux.
Siendo la mas sencilla y recomedada JDeploy, por que automatiza de mejor forma la generación de instaladores,
y trae integrado un mecanismo de actualización automatica para las aplicaciones y tambien integración con github
actions.
El otro mecanismo es utilizando JPackage, que es la forma en la que se generaba anteriormente los instaladores de
PukaHTTP,
es mas complejo y no soporta compilación cruzada, este modo estará deprecado por lo que no se actualizara su documentación
y su forma de trabajo
con puka.

1. [Generar instaladores con JDeploy (Oficial)](docs/jdeploy.md)
2. [Generar instaladores con JPackage (Deprecado)](docs/jpackage.md)




