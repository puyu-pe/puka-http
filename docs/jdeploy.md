# JDeploy
## Indice

1. [Requirimientos](#requirimientos)
2. [Pasos previos](#pasos-previos)
3. [Forma manual](#generar-instaladores-de-forma-manual-en-npm)
4. [Mediante Github Actions](#generar-instaladores-con-github-actions)

## Pasos previos
Ya sea para generar de forma manual o con github actions se tiene
que seguir una serie de pasos antes de hacer el deploy:
1. Ejecutar un git flow hotfix o release
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
   > Nota: En windows modificar la version de package.json de forma manual
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
   git push --all & git push origin $(cat VERSION)
   ```
   > Warning: **No** ejecutar **git push --tags**, ya que puede entrar en conflicto con el tag jdeploy


## Generar instaladores con github actions
* Primero completar [pasos previos](#pasos-previos).
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
1. Importante haber realizado los [pasos previos](#pasos-previos).
2. Ejecutar jdeploy
   ```bash
   jdeploy publish
   ```
   
## Referencias
* [Getting Started JDeploy](https://www.jdeploy.com/docs/manual/#_getting_started)
