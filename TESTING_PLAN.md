## Pruebas Unitarias y Cobertura de Reglas de Negocio

### Reglas de Negocio Críticas del Servicio de Productos
1. **Validación de Stock Disponible:** Comprobar si el inventario actual de un producto cuenta con las existencias físicas suficientes para satisfacer la cantidad exacta solicitada por una transacción externa (`cantidad <= stock`).
2. **Consistencia de Identificadores Únicos (SKU):** Garantizar que las búsquedas y eliminaciones de productos por código SKU operen de manera segura sin importar si el usuario escribe en mayúsculas o minúsculas (`IgnoreCase`).
3. **Gestión de Baja de Productos (Eliminación Segura):** Asegurar que al intentar eliminar un ítem (ya sea por ID o SKU), el sistema verifique primero su existencia previa en la base de datos para responder de forma limpia mediante un booleano, evitando caídas inesperadas del servicio.

### Cobertura Actual

| Regla | Estado | Casos Cubiertos |
| :--- | :--- | :--- |
| 1. Validación de Stock Disponible | ✅ Cubierta | Cantidad menor al stock (caso feliz retorna `true`), Cantidad superior al stock (retorna `false`). |
| 2. Consistencia de SKU (IgnoreCase) | ✅ Cubierta | Existencia de SKU en minúsculas, Búsqueda de SKU en mayúsculas, Eliminación transaccional por SKU. |
| 3. Gestión de Baja de Productos | ✅ Cubierta | Eliminación de ID existente (retorna `true`), Intento con ID o SKU inexistente (retorna `false` de forma segura). |

### Reflexión y Deuda Técnica
- **Riesgo sin probar:** Actualmente se asume que los DTOs de entrada (`InventarioCreateDTO`) llegan sanitizados desde el controlador, pero falta evaluar si se le envía un precio negativo o un SKU vacío.
- **Acción Futura:** Implementar pruebas unitarias de validación de argumentos en el Servicio para capturar excepciones de negocio (`IllegalArgumentException`)
- **Responsable:** Equipo Backend 
