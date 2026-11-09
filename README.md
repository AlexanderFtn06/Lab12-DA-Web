# Laboratorio: Comunicación entre Microservicios

**Alumno:** Alexander Faustino
**Docente:** Ricardo Coello Palomino

---

## Objetivo

Implementar la comunicación entre microservicios utilizando **OpenFeign** y **Eureka Server**, permitiendo que el microservicio `pedido-service` consulte información de `cliente-service` y `producto-service`.

---

## Arquitectura

El proyecto está compuesto por cinco módulos Maven independientes:

| Servicio | Puerto | Base de datos | Descripción |
|---|---|---|---|
| `eureka-server` | 8761 | — | Servidor de registro de servicios |
| `categoria-service` | 8082 | `bd_categoria_dev` | Gestión de categorías |
| `producto-service` | 8083 | `bd_producto_dev` | Gestión de productos (consulta categoría con Feign) |
| `cliente-service` | 8081 | `bd_cliente_dev` | Gestión de clientes |
| `pedido-service` | 8084 | `bd_pedido_dev` | Gestión de pedidos (consulta cliente y producto con Feign) |


---

## Tecnologías

- Java 21
- Spring Boot 3.5.3
- Spring Cloud 2025.0.0 (Eureka Server, Eureka Client, OpenFeign)
- Spring Data JPA
- MySQL 8
- Lombok
- Maven
- Postman (pruebas)

---

## Estructura del proyecto

```
microservicios/
├── eureka-server/
├── categoria-service/
├── cliente-service/
├── producto-service/
└── pedido-service/
```

---

## Requisitos previos

- JDK 21
- IntelliJ IDEA
- MySQL corriendo en `localhost:3306` con usuario `root` (sin contraseña)
- Postman

Crear las bases de datos:

```sql
CREATE DATABASE bd_categoria_dev;
CREATE DATABASE bd_producto_dev;
CREATE DATABASE bd_cliente_dev;
CREATE DATABASE bd_pedido_dev;
```

---

## Ejecución

Ejecutar en este orden:

1. `EurekaServerApplication` (puerto 8761)
2. `CategoriaServiceApplication` (puerto 8082)
3. `ClienteServiceApplication` (puerto 8081)
4. `ProductoServiceApplication` (puerto 8083)
5. `PedidoServiceApplication` (puerto 8084)

Verificar que los cuatro servicios aparezcan en estado **UP** en http://localhost:8761.

---

## Endpoints

### categoria-service (8082)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/categorias` | Listar categorías |
| GET | `/api/categorias/{id}` | Obtener categoría por id |
| POST | `/api/categorias` | Registrar categoría |

### cliente-service (8081)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/clientes` | Listar clientes |
| GET | `/api/clientes/{id}` | Obtener cliente por id |
| POST | `/api/clientes` | Registrar cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |

### producto-service (8083)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar productos |
| GET | `/api/productos/{id}` | Obtener producto con su categoría |
| POST | `/api/productos` | Registrar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### pedido-service (8084)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Listar pedidos |
| GET | `/api/pedidos/{id}` | Obtener pedido con cliente y producto |
| POST | `/api/pedidos` | Registrar pedido |
| PUT | `/api/pedidos/{id}` | Actualizar pedido |
| PUT | `/api/pedidos/{id}/anular` | Anular pedido (no se elimina) |

---

## Ejemplos de uso

**Registrar categoría**

```http
POST http://localhost:8082/api/categorias
Content-Type: application/json

{
  "nombre": "Tecnología",
  "estado": "ACTIVO"
}
```

**Registrar cliente**

```http
POST http://localhost:8081/api/clientes
Content-Type: application/json

{
  "nombre": "Alexander",
  "apellidos": "Faustino",
  "direccion": "Av. Javier Prado 123",
  "estado": "ACTIVO",
  "nroDni": "60803133"
}
```

**Registrar producto**

```http
POST http://localhost:8083/api/productos
Content-Type: application/json

{
  "nombre": "Laptop",
  "precio": 2500,
  "stock": 10,
  "categoriaId": 1
}
```

**Registrar pedido**

```http
POST http://localhost:8084/api/pedidos
Content-Type: application/json

{
  "clienteId": 6,
  "productoId": 4,
  "cantidad": 2
}
```

**Consultar pedido (comunicación entre servicios)**

```http
GET http://localhost:8084/api/pedidos/1
```

Respuesta:

```json
{
  "id": 1,
  "cantidad": 2,
  "cliente": {
    "id": 6,
    "nombre": "Alexander",
    "apellido": "Faustino"
  },
  "producto": {
    "id": 4,
    "nombre": "Pc gamer",
    "precio": 5500.0
  }
}
```

---

## Validaciones

| Entidad | Reglas |
|---|---|
| Pedido | `clienteId` obligatorio, `productoId` obligatorio, `cantidad` mayor a cero |
| Cliente | `nombre`, `apellidos`, `direccion`, `estado` y `nroDni` obligatorios; `nroDni` de 8 dígitos exactos |
| Producto | `nombre` obligatorio, `precio` mayor a cero |

---

## Consideraciones

- No se usan relaciones JPA entre microservicios.
- Cada servicio tiene su propia base de datos.
- No se realizan consultas directas a bases de datos de otros servicios.
- La comunicación se realiza exclusivamente mediante APIs REST y OpenFeign.

---

## Conclusiones

1. Eureka permite localizar los servicios por nombre, sin depender de IPs o puertos fijos.
2. OpenFeign reduce el código necesario para llamar a otros servicios REST.
3. Guardar solo identificadores en `pedido-service` evita duplicar datos de clientes y productos.
4. La validación en la entrada evita registrar datos inválidos y devuelve errores claros.
5. Cada microservicio con su propia base de datos permite desplegarlos y mantenerlos de forma independiente.