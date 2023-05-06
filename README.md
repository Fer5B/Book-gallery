# Book Gallery
API REST para una aplicación de una galería de libros o biblioteca.
</br></br>

### Compilación e implementación del proyecto
```
mvn clean install
```

Este es un proyecto de Spring Boot, por lo que se implementa simplemente usando la clase principal: `DemoApplication.java`.

El proyecto utiliza JDK 19 y la librería Lombok.

Se puede acceder a la api mediante la url:
```
https://localhost:8080/api/books  
```
Se utiliza la especificación OpenApi de Swagger para documentar la API REST por lo que puede acceder a su herramienta de interfaz de usuario:
```
https://localhost:8080/bg-doc-ui
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
##### Uri: `/api/books`

##### Parámetros:
* page = Número de página de la paginación de libros de la API.
* size = Tamaño de la página devuelta, es decir, la cantidad de entidades de retorno.
* title = Filtro de búsqueda por título del libro.
* author = Filtro de búsqueda por autor del libro.
* startPrice = Obtener libros con un precio igual o mayor a este valor.
* endPrice = Obtener libros con un precio igual o menor a este valor.
* releaseDataFrom = Obtener libros a partir de la fecha indicada. Formato: dd-mm-yyyy
* releaseDateTo = Obtener libros hasta la fecha indicada. Formato: dd-mm-yyyy
* sortBy = Mediante este parámetro se puede realizar una ordenación múltiple de los libros obtenidos. Se envía una cadena de texto indicando la propiedad seguida de dos puntos y la dirección de ordenación (ASC: orden ascendente, DESC: orden descendente), cada par 'propiedad:direcciónDeOrdenación' debe ser separado por coma, por ejemplo: 'title:ASC,author:ASC,price:ASC,releaseDate:ASC'. El orden de importancia es de izquierda a derecha.


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
