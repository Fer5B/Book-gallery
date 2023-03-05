# Book Gallery
API REST para una aplicación de una galería de libros o biblioteca.
</br></br>

### Compilación e implementación del proyecto
```
mvn clean install
```

Este es un proyecto de Spring Boot, por lo que se implementa simplemente usando la clase principal: `DemoApplication.java`.

El proyecto utiliza la librería Lombok por lo que asegurese de poder utilizarla.

Una vez implementada, puede acceder a la api mediante la url:
```
https://localhost:8080/api/books  
```
Se utiliza la especificación OpenApi de Swagger para documentar la API REST por lo que puede acceder a su herramienta de interfaz de usuario:
```
https://localhost:8080/swagger-ui
```

## API REST
### POST - Crear libro
##### Uri: `/api/books`
##### Body:
```json
{
  "title": "Título",
  "author": "Autor",
  "releaseDate": "Fecha de publicación",
  "price": 1000
}
```
<hr>

### GET
### Obtener lista de libros
##### Uri: `/api/books?page=0`

##### Parámetros:
* page = Número de página de la paginación de libros de la API. Cada página contendrá a un total de 10 entidades de libro.


### Obtener libro por id
##### Uri: `/api/books/{id}`
<hr>

### PUT - Actualizar libro
##### Uri: `/api/books/{id}`
##### Body:
```json
{
  "title": "Título",
  "author": "Autor",
  "releaseDate": "Fecha de publicación",
  "price": 1000
}
```
<hr>

### PATCH - Actualizar el precio de un libro
##### Uri: `/api/books/{id}`
##### Request body -> price = cadena de texto que representa el valor del nuevo precio del libro
##### Body:
```
"1000"
```
<hr>

### DELETE - Eliminar libro
##### Uri: `/api/books/{id}`
