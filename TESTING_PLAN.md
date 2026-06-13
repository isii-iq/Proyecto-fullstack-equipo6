## Pruebas Unitarias y Cobertura de Reglas de Negocio

### Reglas de Negocio Críticas del Servicio de Productos
1. **Validación de Stock Disponible:** Comprobar si el inventario actual de un producto cuenta con las existencias físicas suficientes solicitadas por una transacción externa 
2. **Consistencia de SKU:** Garantizar que las búsquedas de productos por código SKU operen de manera segura sin importar si el usuario escribe en mayúsculas o minúsculas.
3. **Gestión de Baja de Productos:** Asegurar que al intentar eliminar un ítem (ya sea por ID o SKU), el sistema verifique primero su existencia en la base de datos.

### Cobertura Actual

| Regla | Estado | Casos Cubiertos |
| :--- | :--- | :--- |
| 1. Validación de Stock Disponible | ✅ Cubierta | Cantidad menor al stock, Cantidad superior al stock. |
| 2. Consistencia de SKU | ✅ Cubierta | Existencia de SKU en minúsculas, Búsqueda de SKU en mayúsculas |
| 3. Gestión de Baja de Productos | ✅ Cubierta | Eliminación de ID existente, Intento con ID o SKU inexistente. |

### Reflexión y Deuda Técnica
- **Riesgo sin probar:** Actualmente se asume que los DTOs de entrada (`InventarioCreateDTO`) llegan sanitizados desde el controlador, pero falta evaluar si se le envía un precio negativo o un SKU vacío.
- **Acción Futura:**
- **Responsable:** Equipo Backend 
