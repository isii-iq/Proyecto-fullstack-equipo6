## Pruebas Unitarias y Cobertura de Reglas de Negocio

### Reglas de Negocio Críticas del Servicio de Productos
1. Validación de Sku: no se debe registrar un producto en catalogo si no existe su sku en el inventario
2. Integridad de SKU inventario: El código SKU es obligatorio y no puede duplicarse en la base de datos
3. Integridad de SKU catalogo: El código SKU es obligatorio y no puede duplicarse en la base de datos
4. Validación de Precios inventario: El precio del producto no puede ser inferior a 0.0
5. Validación de Precios: El precio es obligatorio y debe ser mayor a cero. 
6. Campos Obligatorios de Catálogo:Todo producto debe registrarse obligatoriamente con un nombre y una categoría
7. 

### Cobertura Actual
| Regla                  | Estado          | Casos Cubiertos                              |
|------------------------|-----------------|----------------------------------------------|
| 1. Validación de Stock | ✅ Cubierta     | Stock suficiente (feliz), Stock 0 (error)    |
| 2. Cálculo de Precio   | ✅ Cubierta     | 1 producto, múltiples productos              |
| 3. Límite de Compra    | ⚠️ Pendiente   | Solo caso feliz (< 10 unidades)               |

### Reflexión y Deuda Técnica
- Riesgo sin probar: La regla de límite de compra no tiene test de caso de error.
- Acción Futura: Agregar test para el caso de error de la Regla 3 (pedido de 11+ unidades).
- Responsable: Equipo Backend · Sprint 4
