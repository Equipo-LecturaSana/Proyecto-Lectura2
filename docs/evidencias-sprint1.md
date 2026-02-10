# Evidencias Sprint 1 - Mejoras del Carrito de Compras

## Resumen de Mejoras Implementadas

En este sprint se realizaron mejoras significativas al sistema de carrito de compras, enfocadas en robustez, precisión de cálculos, seguridad y experiencia de usuario.

---

## 1. CarritoItem.java - Modelo Mejorado

### Cambios Principales

#### Precisión Monetaria
- **Cambio de `Double` a `BigDecimal`**: Mayor precisión en cálculos monetarios, evitando errores de redondeo
- Configuración de precisión en base de datos: `precision = 10, scale = 2`

#### Optimización de Base de Datos
```java
@Table(name = "carrito_items", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_session_libro", columnList = "session_id, libro_id")
})
```
- Índices agregados para mejorar el rendimiento de consultas frecuentes
- Búsqueda por sesión y por sesión+libro optimizadas

#### Validaciones Jakarta Bean Validation
- `@NotNull`, `@NotBlank`: Campos obligatoriosñ
- `@Size`: Límites de longitud de texto
- `@DecimalMin`: Precio debe ser mayor a 0
- `@Min`, `@Max`: Cantidad entre 1 y 999

#### Nuevos Métodos Útiles

**calcularSubtotal()**
```java
public BigDecimal calcularSubtotal() {
    if (precio == null || cantidad == null) {
        return BigDecimal.ZERO;
    }
    return precio.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
}
```

**incrementarCantidad() / decrementarCantidad()**
```java
public void incrementarCantidad() {
    if (this.cantidad == null) {
        this.cantidad = 1;
    } else {
        this.cantidad++;
    }
}
```

**esValido()**
```java
public boolean esValido() {
    return libroId != null &&
           titulo != null && !titulo.trim().isEmpty() &&
           precio != null && precio.compareTo(BigDecimal.ZERO) > 0 &&
           cantidad != null && cantidad > 0 &&
           sessionId != null && !sessionId.trim().isEmpty();
}
```

**toString()**
- Mejor representación para debugging y logging

---

## 2. CarritoService.java - Lógica de Negocio Robusta

### Cambios Principales

#### Uso de BigDecimal
- Todos los cálculos monetarios usan `BigDecimal` con precisión de 2 decimales
- `RoundingMode.HALF_UP` para redondeo consistente

#### Sistema de Excepciones Específicas
- `IllegalArgumentException`: Datos inválidos (IDs nulos, valores incorrectos)
- `IllegalStateException`: Problemas de estado (stock insuficiente, carrito vacío)
- Mensajes de error descriptivos y específicos

#### Límites de Seguridad
```java
private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
private static final int MAX_CANTIDAD_POR_ITEM = 50;
```
- Límite configurable de 50 unidades por item
- Previene abusos del sistema

#### Nuevos Métodos de Servicio

**calcularSubtotal(String sessionId)**
```java
public BigDecimal calcularSubtotal(String sessionId) {
    validarSessionId(sessionId);
    return obtenerCarritoPorSession(sessionId).stream()
            .map(CarritoItem::calcularSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
}
```

**calcularIgv(String sessionId)**
```java
public BigDecimal calcularIgv(String sessionId) {
    BigDecimal subtotal = calcularSubtotal(sessionId);
    return subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
}
```

**calcularTotal(String sessionId)**
```java
public BigDecimal calcularTotal(String sessionId) {
    BigDecimal subtotal = calcularSubtotal(sessionId);
    BigDecimal igv = calcularIgv(sessionId);
    return subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);
}
```

**contarItemsEnCarrito(String sessionId)**
```java
public int contarItemsEnCarrito(String sessionId) {
    validarSessionId(sessionId);
    return obtenerCarritoPorSession(sessionId).stream()
            .mapToInt(CarritoItem::getCantidad)
            .sum();
}
```

**carritoEstaVacio(String sessionId)**
- Verificación de estado del carrito

#### Transacciones Optimizadas
```java
@Transactional(readOnly = true)
public BigDecimal calcularTotal(String sessionId) {
    // Operación de solo lectura optimizada
}
```

#### Validación de Stock Mejorada
```java
public void agregarAlCarrito(String sessionId, Long libroId, String titulo, BigDecimal precio, String imagen) {
    // Validar sesión y datos
    validarSessionId(sessionId);
    validarDatosLibro(libroId, titulo, precio);

    // Verificar stock actual
    Libro libro = libroService.obtenerPorId(libroId)
        .orElseThrow(() -> new IllegalArgumentException("El libro con ID " + libroId + " no existe"));

    if (libro.getStock() <= 0) {
        throw new IllegalStateException("El libro '" + titulo + "' está agotado");
    }

    // Validar límite de cantidad
    if (nuevaCantidad > libro.getStock()) {
        throw new IllegalStateException("Stock insuficiente para '" + titulo + "'. Disponible: " + libro.getStock());
    }

    if (nuevaCantidad > MAX_CANTIDAD_POR_ITEM) {
        throw new IllegalStateException("No puedes agregar más de " + MAX_CANTIDAD_POR_ITEM + " unidades del mismo libro");
    }
}
```

#### Métodos de Validación Privados
```java
private void validarSessionId(String sessionId) {
    if (sessionId == null || sessionId.trim().isEmpty()) {
        throw new IllegalArgumentException("El ID de sesión no puede estar vacío");
    }
}

private void validarDatosLibro(Long libroId, String titulo, BigDecimal precio) {
    if (libroId == null) {
        throw new IllegalArgumentException("El ID del libro no puede ser nulo");
    }
    if (titulo == null || titulo.trim().isEmpty()) {
        throw new IllegalArgumentException("El título del libro no puede estar vacío");
    }
    if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("El precio del libro debe ser mayor a cero");
    }
}
```

---

## 3. CarritoController.java - Controlador Profesional

### Cambios Principales

#### Variables con Nombres Descriptivos
**Antes:**
```java
private final CarritoService cs;
private final PedidoService ps;
private final UsuarioService us;
HttpSession s, Model m
```

**Después:**
```java
private final CarritoService carritoService;
private final PedidoService pedidoService;
private final UsuarioService usuarioService;
HttpSession session, Model model
```

#### Logging con SLF4J
```java
private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);

logger.info("Pedido procesado exitosamente. ID: {}", pedidoForm.getId());
logger.warn("Error de validación al agregar al carrito: {}", e.getMessage());
logger.error("Error inesperado al procesar pedido", e);
```

#### Manejo de Errores Diferenciado
```java
@PostMapping("/carrito/agregar")
@ResponseBody
public ResponseEntity<Map<String, Object>> agregarAlCarrito(...) {
    try {
        // Lógica de negocio
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        logger.warn("Error de validación al agregar al carrito: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "message", e.getMessage()));
    } catch (IllegalStateException e) {
        logger.warn("Error de estado al agregar al carrito: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("success", false, "message", e.getMessage()));
    } catch (Exception e) {
        logger.error("Error inesperado al agregar al carrito", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Error al agregar el libro al carrito"));
    }
}
```

#### Respuestas AJAX Completas
```java
Map<String, Object> response = new HashMap<>();
response.put("success", true);
response.put("message", "¡Libro añadido al carrito!");
response.put("cantidadTotal", cantidadTotal); // Nueva información para actualizar badge
```

#### Mejores Mensajes Flash
```java
redirectAttributes.addFlashAttribute("mensaje",
    "¡Compra realizada exitosamente! Número de pedido: " + pedidoForm.getId());

redirectAttributes.addFlashAttribute("error",
    "Stock insuficiente para '" + item.getTitulo() + "'. Disponible: " + stock);
```

#### Validación Mejorada de Tarjeta
```java
try {
    if (YearMonth.parse(pedidoForm.getFechaVencimiento()).isBefore(YearMonth.now())) {
        bindingResult.rejectValue("fechaVencimiento", "error", "La tarjeta está vencida.");
    }
} catch (Exception e) {
    bindingResult.rejectValue("fechaVencimiento", "error", "Formato de fecha inválido (usar MM/yyyy).");
}
```

#### Documentación de Métodos
```java
/**
 * Muestra la página del carrito de compras
 */
@GetMapping("/carrito")
public String mostrarCarrito(HttpSession session, Model model) {
    // ...
}

/**
 * Agrega un libro al carrito (endpoint AJAX)
 */
@PostMapping("/carrito/agregar")
@ResponseBody
public ResponseEntity<Map<String, Object>> agregarAlCarrito(...) {
    // ...
}

/**
 * Procesa el pago y crea el pedido
 */
@PostMapping("/carrito/procesar")
public String procesarPedido(...) {
    // ...
}
```

---

## 4. Mejoras de Seguridad y Rendimiento

### Seguridad

#### Validación de Entrada
- Todos los endpoints validan parámetros de entrada
- IDs no pueden ser nulos
- Strings no pueden estar vacíos
- Cantidades deben estar en rangos válidos

#### Prevención de Valores Negativos
```java
if (cantidad != null && cantidad <= 0) {
    carritoItemRepository.deleteById(itemId);
    return;
}
```

#### Límite de Cantidad por Item
```java
private static final int MAX_CANTIDAD_POR_ITEM = 50;

if (nuevaCantidad > MAX_CANTIDAD_POR_ITEM) {
    throw new IllegalStateException("No puedes agregar más de " + MAX_CANTIDAD_POR_ITEM + " unidades");
}
```

### Rendimiento

#### Índices de Base de Datos
```java
@Table(name = "carrito_items", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_session_libro", columnList = "session_id, libro_id")
})
```
- Consultas por sesión: O(log n) en lugar de O(n)
- Búsqueda de item específico por sesión+libro: O(log n)

#### Transacciones Optimizadas
```java
@Transactional(readOnly = true) // Operaciones de solo lectura
public BigDecimal calcularTotal(String sessionId) {
    // No requiere bloqueos de escritura
}

@Transactional // Operaciones de escritura
public void agregarAlCarrito(...) {
    // Gestión automática de transacciones
}
```

#### Uso de Streams
```java
return obtenerCarritoPorSession(sessionId).stream()
        .map(CarritoItem::calcularSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
```

---

## 5. Mejoras en UX (Experiencia de Usuario)

### Mensajes de Error Específicos

**Antes:**
```
"Agotado"
"Stock insuficiente"
"Carrito vacío"
```

**Después:**
```
"El libro 'Cien Años de Soledad' está agotado"
"Stock insuficiente para 'Don Quijote'. Disponible: 5"
"El carrito está vacío"
"No puedes agregar más de 50 unidades del mismo libro"
"Stock insuficiente para 'El Principito'. Cantidad en carrito: 10, Disponible: 8"
```

### Contador de Items Actualizado
```javascript
// Respuesta AJAX incluye cantidadTotal
{
    "success": true,
    "message": "¡Libro añadido al carrito!",
    "cantidadTotal": 5  // Actualiza el badge del carrito
}
```

### Feedback Visual con Mensajes Flash
- Mensajes de éxito: "Cantidad actualizada correctamente"
- Mensajes de error: "Error al actualizar la cantidad"
- Confirmaciones: "¡Compra realizada exitosamente! Número de pedido: 1234"

### Manejo Gracioso de Errores
```java
try {
    // Operación principal
} catch (Exception e) {
    logger.error("Error al mostrar carrito", e);
    model.addAttribute("error", "Error al cargar el carrito: " + e.getMessage());
    return "carrito"; // Aún muestra la página en lugar de error 500
}
```

---

## Resultados y Beneficios

### Robustez
- Sistema más resiliente a errores
- Mejor manejo de casos extremos
- Validaciones exhaustivas en todos los niveles

### Precisión
- Cálculos monetarios exactos con BigDecimal
- Eliminación de errores de redondeo
- Consistencia en resultados financieros

### Mantenibilidad
- Código más legible con nombres descriptivos
- Documentación inline con JavaDoc
- Logging para debugging y monitoreo

### Rendimiento
- Índices de base de datos para consultas rápidas
- Transacciones optimizadas
- Uso eficiente de streams

### Seguridad
- Validación de entrada en todos los endpoints
- Límites configurables para prevenir abusos
- Manejo seguro de excepciones

### Experiencia de Usuario
- Mensajes de error claros y específicos
- Feedback inmediato en operaciones AJAX
- Sistema estable que no rompe en errores

---

## Archivos Modificados

1. `CarritoItem.java` - Modelo de datos
2. `CarritoService.java` - Lógica de negocio
3. `CarritoController.java` - Controlador web
4. `CarritoItemRepository.java` - (sin cambios mayores)

## Compatibilidad

Todas las mejoras son retrocompatibles y no requieren cambios en el frontend existente, excepto:
- El parámetro `precio` en `/carrito/agregar` ahora acepta BigDecimal (compatible con números)
- La respuesta de `/api/carrito/count` ahora incluye `success: true/false`

---

## Próximos Pasos Sugeridos

1. Implementar caché para contadores de carrito
2. Agregar tests unitarios y de integración
3. Implementar límite de tiempo para carritos abandonados
4. Agregar funcionalidad de "guardar para después"
5. Implementar notificaciones cuando items del carrito cambien de precio o stock
