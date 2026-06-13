## Pruebas Unitarias y Cobertura de Reglas de Negocio

### Reglas de Negocio Críticas del Servicio de Productos
1. **Validación de Stock Disponible:** Comprobar si el inventario actual de un producto cuenta con las existencias físicas suficientes solicitadas por una transacción externa 
2. **Consistencia de SKU:** Garantizar que las búsquedas de productos por código SKU operen de manera segura sin importar si el usuario escribe en mayúsculas o minúsculas.
3. **Gestión de Baja de Productos:** Asegurar que al intentar eliminar un ítem (ya sea por ID o SKU), el sistema verifique primero su existencia en la base de datos.
4. **SKU Único (Catalogo)**: No se puede registrar un producto si su SKU ya existe en la base de datos (`existsBySkuIgnoreCase`).
5. **Sincronización de Stock**: Al buscar o guardar un producto, es obligatorio consultar el stock real usando el cliente Feign (`InventarioClient`).
6. **Filtro de Disponibilidad**: Los productos marcados como no disponibles (`disponible = false`) deben ser excluidos de las consultas públicas (`findByDisponibleTrue`).

### Cobertura Actual

| Regla | Estado | Casos Cubiertos |
| :--- | :--- | :--- |
| 1. Validación de Stock Disponible | ✅ Cubierta | Cantidad menor al stock, Cantidad superior al stock. |
| 2. Consistencia de SKU | ✅ Cubierta | Existencia de SKU en minúsculas, Búsqueda de SKU en mayúsculas |
| 3. Gestión de Baja de Productos | ✅ Cubierta | Eliminación de ID existente, Intento con ID o SKU inexistente. |
| 4. SKU Único | ✅ Cubierta | `testExistsBySkuIgnoreCase` en Repository |
| 5. Sincronización de Stock | ✅ Cubierta | `debeRetornarProductoPorIdExistente` y `debeGuardarProductoExitosamente` en Service |
| 6. Filtro de Disponibilidad | ✅ Cubierta | `testFindByDisponibleTrue` en Repository |

### Reflexión y Deuda Técnica
- **Riesgo sin probar:** Actualmente se asume que los DTOs de entrada (`InventarioCreateDTO`) llegan sanitizados desde el controlador, pero falta evaluar si se le envía un precio negativo o un SKU vacío.
- **Acción Futura:** Implementar pruebas unitarias de validación de argumentos en el Servicio para capturar excepciones de negocio (`IllegalArgumentException`)
- **Responsable:** Equipo Backend
- **Simulación de Errores de Red**: El cliente Feign (`InventarioClient`) actualmente solo se prueba en escenarios exitosos (Mocks). Queda pendiente probar cómo reacciona el sistema si el microservicio de Inventario se cae o da un error 500.
- **Validación de Datos en Payload**: Los controladores aceptan los datos directamente. Falta implementar anotaciones de validación (como `@NotNull` o `@Min`) en los DTOs y testear que el controlador rechace precios negativos o campos vacíos con un error 400.
- **Pruebas de Concurrencia**: Falta verificar el comportamiento del catálogo bajo escenarios de alta demanda, asegurando que múltiples peticiones simultáneas modificando la disponibilidad de un mismo producto no generen inconsistencias en la base de datos.
