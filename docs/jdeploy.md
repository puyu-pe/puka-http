# JDeploy

JDeploy es una solución multiplataforma para la creación de instaladores y ofrece un
mecanismo de actualización automatica. Ver mas detalles en su [documentación oficial](https://www.jdeploy.com/docs/manual/#_getting_started).

* Antes de lanzar una nueva versión se recomienda realizar lanzamiento a [versión beta](#lanzamiento-versión-beta-o-pre-release).
* Y cuando todo este correcto con la aplicación en versión beta, realizar [Lanzamiento a producción](#lanzamiento-a-producción).


## Indice

1. [Lanzamiento version beta o Pre-release](#lanzamiento-versión-beta-o-pre-release).
2. [Lanzamiento a producción](#lanzamiento-a-producción).
3. [Referencias](#referencias).

## Lanzamiento versión beta o Pre-release

### Requirimientos
* Se tiene que tener instalado Java 17 o superior.
* Tener instalado npm.
* Tener una [cuenta npm](https://www.npmjs.com/signup).
* Pedir permisos para publicar al autor (cuenta npm de puyu).
  Ser invitado por la cuenta **npm de puyu** como colaborador del proyecto puka-http.
* Instalar JDeploy.
   ```bash
   npm install jdeploy
   ```

### Pasos para publicar (versión beta)
1. Importante!!, primero se debe [preparar una nueva versión](/README.md#preparar-una-nueva-versión-%EF%B8%8F).
2. Login mediante linea de comandos en npm.
   ```bash
   npm login --registry=https://registry.npmjs.org/  --scope=@puyu    
   ```
3. Copiar package.beta.json a package.json
   ```bash
   cp package.beta.json package.json
   ```
4. Empaquetar aplicación.
   ```bash
   mvn clean compile package
   ```
5. Ejecutar jdeploy
   ```bash
   npx jdeploy publish
   ```
6. Si todo sale correctamente, comprobar en la [pagina de descarga, versión beta](https://www.jdeploy.com/~puka-http-beta).
7. Si **PukaHTTP-beta** ya esta instalado, entonces solo cerrar y volver a ejecutar para que se actualice a la nueva versión.
8. Opcionalmente, si aun no esta instalado, tambien se puede instalar mediante npm (solo version beta).
   ```bash
   npm i puka-http-beta
   ```

## Lanzamiento a producción.
* Importante!, Si **previamente NO** se hizo [lanzamiento versión beta](#lanzamiento-versión-beta-o-pre-release), primero se debe [preparar una nueva versión](/README.md#preparar-una-nueva-versión-%EF%B8%8F).
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
