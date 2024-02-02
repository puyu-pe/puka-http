# JDeploy
## Indice

1. [Requirimientos](#requirimientos)
2. [Mediante GitHub Actions](#generar-instaladores-con-github-actions)
3. [Forma manual](#generar-instaladores-de-forma-manual-en-npm)
4. [Referencias](#referencias)

## Generar instaladores con GitHub actions
* Importante!!, primero completar [flujo de trabajo para lanzar una nueva versión](/README.md#preparar-una-nueva-versión).
* En la pestaña Actions del proyecto ejecutar la ["jDeploy CI with maven"](https://github.com/puyu-pe/puka-http/actions/workflows/jdeploy-manual.yml) action
haciendo click en el boton run workflow en la tag mas reciente.
  > Importate!!!: Run workflow no tiene que ejecutarse en ninguna rama, solo en la ultima tag
  > generada con git flow hotfix o release.
* Luego en [releases](https://github.com/puyu-pe/puka-http/releases) publicar el ultimo release que estara en draft.
  > Nota: Esto es importante por que si no, no estara correctamente publicado los instaladores
* Finalmente cuando la action workflow termina de ejecutarse, la nueva version estara disponible en la [Pagina de descarga](https://www.jdeploy.com/gh/puyu-pe/puka-http).

## Generar instaladores de forma manual en npm
* Esta forma de generar los instaladores esta destinada al admin de la cuenta npm de puyu.
* La generación manual requiere que tu cuenta npm este registrada en la organización puyu en npm.
* En el proyecto ya viene configurado los archivos package.json, icon.png  y installsplash.png que son necesarios para 
jdeploy.

### Requirimientos
* Se tiene que tener instalado Java 17 o superior.
* Tener instalado npm.
* Tener una [cuenta npm](https://www.npmjs.com/signup).
* Pedir permisos para publicar al autor (cuenta npm de puyu)
  Se tiene que asociar tu cuenta a la de la empresa con permisos de publicar packages.
* Instalar JDeploy.
   ```bash
   npm install -g jdeploy
   ```

### Pasos para publicar
1. Importante!!, primero realizar el [flujo de trabajo para lanzar una nueva versión](/README.md#preparar-una-nueva-versión).
2. Ejecutar jdeploy
   ```bash
   jdeploy publish
   ```
   
## Referencias
* [Getting Started JDeploy](https://www.jdeploy.com/docs/manual/#_getting_started)
