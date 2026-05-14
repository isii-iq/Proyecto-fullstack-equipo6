# Proyecto Semestral: Tienda E-Commerce

## Integrantes
- Isidora Quinteros
- Matias Yañez

## Estado del Sistema (Hito 1.5)

| Microservicio  | Puerto | DB Name  | Funcionalidad         |
| :------------- | :----- | :------- | :-------------------- |
| Clientes-Service   | 8081   | clientes_db  |CRUD de cliente|
| Catalogo-Service  | 8082   | catalogo_db    | CRUD de producto     |
| Inventario-Service   | 8083   | inventario_db    | CRUD de inventario     |
| Pedidos-Service | 8084   | pedidos_db   | CRUD de Pedido   |
| Pagos-Service  | 8085   | pagos_db     | CRUD de pago    |

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
- Cliente REST: **Feign Client** (justificación: ...)
- Manejo de errores: `@ControllerAdvice` + excepciones personalizadas
- Logs: SLF4J en cada llamada externa
- Pruebas de integración: colección Postman en `/postman/hito2-integracion.json`

### Escenario de despliegue
- [ ] Escenario A — Todos los servicios en una sola instancia EC2
- [ ] Escenario B — Servicios distribuidos en múltiples instancias EC2
  - IPs y puertos por servicio: ...
  - Security Groups configurados: sí/no

### Cómo probar la integración
1. Levantar todos los servicios: `docker compose up -d`
2. Importar `postman/hito2-integracion.json` en Postman
3. Ejecutar el flujo "Crear cita - caso éxito"
4. Para probar resiliencia: `docker stop pacientes-app` y reintentar
