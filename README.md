# Microservicios con Spring Boot — GLAB S11

Laboratorio de **Desarrollo de Aplicaciones Web** (Tecsup). Sistema de ventas dividido en 4 microservicios independientes, cada uno con su propia base de datos y su propio puerto.

## Arquitectura

| Microservicio | Puerto | Base de datos | Endpoint base |
|---|---|---|---|
| cliente-service | 8081 | `bd_cliente` | `/api/clientes` |
| categoria-service | 8082 | `bd_categoria` | `/api/categorias` |
| producto-service | 8083 | `bd_producto` | `/api/productos` |
| pedido-service | 8084 | `bd_pedido` | `/api/pedidos` |

Cada servicio es una aplicación Spring Boot que se ejecuta por separado. No comparten base de datos, por lo que las referencias entre servicios se guardan solo como IDs:

- `Producto.categoriaId` → categoría en categoria-service
- `Pedido.idCliente` → cliente en cliente-service
- `DetallePedido.idProducto` → producto en producto-service

La única relación JPA está dentro de pedido-service: `Pedido` (1) → `DetallePedido` (N).


## Estructura de cada proyecto

```
microservicios/
├── categoria-service/
├── cliente-service/
├── pedido-service/
└── producto-service/
    └── src/main/java/com/tecsup/
        ├── controller/    endpoints REST
        ├── dto/           datos de entrada con validaciones
        ├── exception/     excepciones y manejo global de errores
        ├── model/         entidades JPA
        ├── repository/    acceso a datos (JpaRepository)
        ├── services/      lógica de negocio
        └── ...ServiceApplication.java
```

## Bases de datos

```sql
CREATE DATABASE bd_categoria;
CREATE DATABASE bd_producto;
CREATE DATABASE bd_cliente;
CREATE DATABASE bd_pedido;

-- Perfil de desarrollo
CREATE DATABASE bd_categoria_dev;
CREATE DATABASE bd_producto_dev;
CREATE DATABASE bd_cliente_dev;
CREATE DATABASE bd_pedido_dev;
```

Las tablas las genera Hibernate con `ddl-auto: update`.

## Endpoints

Todos los servicios exponen el mismo CRUD:

| Método | Ruta | Acción |
|---|---|---|
| GET | `/api/{recurso}` | Listar |
| GET | `/api/{recurso}/{id}` | Buscar por ID |
| POST | `/api/{recurso}` | Registrar |
| PUT | `/api/{recurso}/{id}` | Actualizar |
| DELETE | `/api/{recurso}/{id}` | Eliminar |

### Validaciones y errores

categoria, cliente y producto usan DTOs con Bean Validation y un `GlobalExceptionHandler`:

- **400 Bad Request** si un campo no cumple su validación (`@NotBlank`, `@NotNull`, `@Positive`, `@Pattern`)
- **404 Not Found** si el ID no existe (`ResourceNotFoundException`)

Validaciones destacadas: DNI de 8 dígitos y teléfono de 9 dígitos en cliente; precio mayor a 0 y stock no negativo en producto.

pedido-service sigue el diseño de la guía: recibe la entidad directamente y lanza `RuntimeException` (responde 500 ante un ID inexistente).


## Configuración con YAML

Los 4 servicios usan `application.yml` en lugar de `application.properties`. Ejemplo de categoria-service:

```yaml
server:
  port: 8082

spring:
  application:
    name: categoria-service
  datasource:
    url: jdbc:mysql://localhost:3306/bd_categoria
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQLDialect
```

## Perfiles dev y prod

En categoria-service la configuración se separó por ambiente:

| Archivo | Contenido |
|---|---|
| `application.yml` | Nombre del servicio y perfil activo (`dev`) |
| `application-dev.yml` | Puerto 8082, base `bd_categoria_dev` |
| `application-prod.yml` | Puerto 9092, base en `servidor/bd_categoria` |

Para cambiar de ambiente solo se modifica `spring.profiles.active`. Se verificó que con el perfil `dev` los registros se guardan en `bd_categoria_dev`, sin afectar `bd_categoria`.

