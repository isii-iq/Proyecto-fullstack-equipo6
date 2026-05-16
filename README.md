# Proyecto Semestral: Tienda E-Commerce

## Integrantes
- Isidora Quinteros
- Matias Yañez

## Estado del Sistema (Hito 1.5)

| Microservicio  | Puerto | DB Name  | Funcionalidad         |
| :------------- | :----- | :------- | :-------------------- |
| Clientes-Service   | 8081   | clientes_db  |Gestión de perfiles|
| Catalogo-Service  | 8082   | catalogo_db    | Administración de productos y categorías |
| Inventario-Service   | 8083   | inventario_db    |Control de existencias y de stock.|
| Pedidos-Service | 8084   | pedidos_db   | Orquestación de compras y estados de orden   |
| Pagos-Service  | 8085   | pagos_db     | Procesamiento de pagos.   |
| Carrito-Service   | 8086 | carrito_db  |Gestion compra de productos por usuario.|
| Cupones-Service  | 8087   | cupones_db    | Validación de descuento y promociones. |
| Envios-Service   | 8088   | envios_db    | Seguimiento y despacho de productos.|
| Notificaciones-Service | 8089   | notificacioness_db   | Gestión de alertas  |
| Reseñas-Service  | 8090   | reseñas_db     | opiniones y calificación de productos. |

## Despliegue Técnico
- **Instancia:** AWS EC2 t3.large (Ubuntu 24.04)
- **Comando de inicio:** `docker compose up -d`
- **Repositorio Maestro:** [[URL]](https://github.com/isii-iq/Proyecto-fullstack-equipo6)

  ## 🔗 Comunicación entre microservicios (Hito 2)

### Diagrama de dependencias
<img width="1011" height="711" alt="image" src="https://github.com/user-attachments/assets/0e230967-b1dc-4841-a538-08c5d794e19c" />



## Tabla de contratos
| Origen | Destino | Método | Endpoint | DTO |
|---|---|---|---|---|
| Catalogo | Inventario | GET | /api/producto/{id} | ProductoDTO |
| Envios | Pedidos | GET | /api/inventario/{id} | PedidoDTO |
| Pedidos | Clientes | GET | /api/cliente/{id} | ClienteDTO |
| Pedidos | Carrito | GET | /api/carrito/{id} | CarritoDTO |
| Pedidos | Catalogo | GET | /api/producto/{id} | ProductoDTO |
| Notificaciones | Pedidos | GET | /api/pedido/{id} | PedidoDTO |
| Notificaciones | Clientes | GET | /api/cliente/{id} | ClienteDTO |
| Reseñas | Catalogo | GET | /api/producto/{id} | ProductoDTO |
| Reseñas | Clientes | GET | /api/cliente/{id} | ClienteDTO |
| Pagos | Pedidos | GET | /api/pedido/{id} | PedidoDTO |
| Cupones | Pedidos | GET | /api/pedido/{id} | PedidoDTO |



### Tecnología utilizada
- Cliente REST: **Feign Client** Se seleccionó OpenFeign por su enfoque declarativo, que permite integrar microservicios mediante interfaces anotadas de Spring Cloud. Facilita el mantenimiento de los contratos y permite una integración nativa con el manejo de excepciones y validaciones de Spring Boot.
- Manejo de errores: `@ControllerAdvice` + excepciones personalizadas
- Logs: SLF4J en cada llamada externa
- Pruebas de integración: colección Postman en `/postman/hito2-integracion.json`

### Escenario de despliegue
  * Clientes-Service: `${CLIENTEShttp://entorno-clientes:8081}`
  * Catalogo-Service: `${CATALOGO_URL:http://entorno-catalogo:8082}`
  * Inventario-Service: `${INVENTARIO_URL:http://entorno-inventario:8083}`
  * Pedidos-Service: `${PEDIDOS_URL:http://entorno-pedidos:8084}`
  * Pagos-Service: `${PAGOS_URL:http://entorno-pagos:8085}`
  * Carrito-Service: `${CARRITO_URL:http://entorno-carrito:8086}`
  * Cupones-Service: `${CUPONES_URL:http://entorno-cupones:8087}`
  * Envios-Service: `${ENVIOS_URL:http://entorno-envios:8088}`
  * Notificaciones-Service: `${NOTIFICACIONES_URL:http://entorno-notificaciones:8089}`
  * Reseñas-Service: `${RESENAS_URL:http://entorno-resenas:8090}`
* **Security Groups configurados:** sí (Inbound Rules abiertas para los puertos 8081-8090, puerto 80 para http y puerto 22 para SSH).

### Cómo probar la integración
1. Levantar todos los servicios: `docker compose up -d`
2. Importar `postman/hito2-integracion.json` en Postman
3. Ejecutar el flujo "Post Pedido" en Postman.
4. Para probar resiliencia: Ejecutar `docker stop pedidos-service y reintentar
