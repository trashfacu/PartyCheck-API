# Equipo 01

## Backend

#### Paso a paso para levantar el entorno de desarrollo de back y poder realizar las pruebas

primero clonar el repositorio y cambiar la rama a "develop"

`git clone git@gitlab.ctd.academy:ctd/hispanos/proyecto-integrador-1/proyecto-integrador-0723/0222pt-c5/equipo-01.git`

`git checkout develop`

Luego hay que levantar la base de datos para que la aplicacion corra y realizar las pruebas.

### En caso de querer usar SQl Local

Primero copiar el script que figura en `/assets/create_db.sql` para levantar la db en mySqlWorkbench u otro motor que utilice sql.

Despues cambiar en el archivo `application.properties` la siguiente linea

`spring.datasource.url=jdbc:mysql://localhost:3306/party_check_db`

por la direccion nueva de tu sql en local, por ejemplo:

`spring.datasource.url=jdbc:h2:~/party_check_db`
 
### En caso de querer utilizar Docker

Abrir una terminal y situarnos sobre el directorio que este el Dockerfile, en este caso `/Backend` y correr el siguiente comando:

`docker build -t party_check_imagedb`

Podemos verificar la correcta creacion con `docker images`

Luego hay que crear el contenedor con el siguiente comando:

`docker run -d --name party_check_db -p 3306:3306 party_check_imagedb`

Verificamos con el comando `docker ps -l`

Lo inicializamos con `docker start party_check_db`

En caso de querer entrar al contenedor y realizar alguna accion con el comando

`docker exec -it party_check_db mysql -u root -p`
