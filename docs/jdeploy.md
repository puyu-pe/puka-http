# JDeploy

JDeploy es una solución multiplataforma para la creación de instaladores y ofrece un
mecanismo de actualización automatica. Ver mas detalles en su [documentación oficial](https://www.jdeploy.com/docs/manual/#_getting_started).

* Antes de lanzar una nueva versión se recomienda realizar los pasos de [Pre-release](#lanzamiento-en-desarrollo-pre-release).
* Y cuando todo este correcto con la aplicación en versión de desarrollo realizar [Lanzamiento a producción](#lanzamiento-a-producción).


## Indice

1. [Lanzamiento en desarrollo o Pre-release](#lanzamiento-en-desarrollo-pre-release).
2. [Lanzamiento a producción](#lanzamiento-a-producción).
3. [Referencias](#referencias).

## Lanzamiento en desarrollo, Pre-release

### Requirimientos
* Se tiene que tener instalado Java 17 o superior.
* Tener instalado npm.
* Tener una [cuenta npm](https://www.npmjs.com/signup).
* Pedir permisos para publicar al autor (cuenta npm de puyu).
  Asociar cuenta a la de la empresa como miembro del proyecto.
* Logerse mediante linea de comandos en npm.
   ```bash
   npm login --registry=https://registry.npmjs.org/  --scope=@puyu    
   ```
* Instalar JDeploy.
   ```bash
   npm install jdeploy
   ```

### Pasos para publicar (desarrollo)
1. Importante!!, primero realizar el [flujo de trabajo para lanzar una nueva versión](/README.md#preparar-una-nueva-versión).
2. Copiar package.develop.json a package.json
   ```bash
   cp package.develop.json package.json
   ```
3. Ejecutar jdeploy
   ```bash
   npx jdeploy publish
   ```
4. Si todo sale correctamente, comprobar en la [pagina de descarga, versión develop](https://www.jdeploy.com/~puka-http-dev).
5. Si puka ya esta instalado, entonces solo cerrar y volver a ejecutar Puka para que se actualice a la nueva versión.
5. Opcionalmente, si aun no esta instalado, tambien se puede instalar mediante npm (solo version desarrollo).
   ```bash
   npm i puka-http-dev
   ```

## Lanzamiento a producción.
* Importante!, Si **previamente NO** se hizo [Pre-release](#lanzamiento-en-desarrollo-pre-release), primero completar [flujo de trabajo para lanzar una nueva versión](/README.md#preparar-una-nueva-versión).
* En la pestaña Actions del proyecto ejecutar la ["jDeploy CI with maven"](https://github.com/puyu-pe/puka-http/actions/workflows/jdeploy-manual.yml) action
  haciendo click en el boton run workflow en la tag mas reciente.
  > Importate!!!: Run workflow no tiene que ejecutarse en ninguna rama, solo en la ultima tag
  > generada con git flow hotfix o release.
* Luego en [releases](https://github.com/puyu-pe/puka-http/releases) publicar el ultimo release que estara en draft.
  > Nota: Esto es importante por que si no, no estara correctamente publicado los instaladores
* Finalmente cuando el workflow termina de ejecutarse, la nueva version estara disponible en la [Pagina de descarga](https://www.jdeploy.com/gh/puyu-pe/puka-http).
En la cual se puede descargar la nueva versión o si ya estaba previamente instalado entonces solo cerrar y volver a abrir Puka 
para que se actualice a la nueva versión.


## Referencias
* [Getting Started JDeploy](https://www.jdeploy.com/docs/manual/#_getting_started)
