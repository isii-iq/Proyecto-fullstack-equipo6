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
