# Historias de Usuario Técnicas – LecturaSana

Documento generado a partir del análisis de la estructura actual del proyecto (controladores, modelos, servicios, configuración y flujos existentes). Formato: **Como [rol], quiero [acción] para [beneficio]** con 3 Criterios de Aceptación por historia.

---

## Resumen del análisis del codebase

| Área | Componentes analizados |
|------|------------------------|
| **Configuración** | `pom.xml` (Spring Boot 3.5.6, Security, JPA, MySQL, Thymeleaf, Validation), `application.properties` (MySQL, JPA create-drop, sesión 30 min), `SecurityConfig` (rutas públicas/admin, form login, logout) |
| **Autenticación** | `LoginController`, `RegistroController`, `UsuarioService`, `Usuario` (UserDetails, validaciones), `Rol`, `MayorDeEdadValidator` |
| **Catálogo** | `CatalogoController`, `LibroController` (admin), `LibroService`, `Libro`, paginación y filtro por categoría/búsqueda |
| **Carrito y pedidos** | `CarritoController`, `CarritoService`, `Pedido`, `CarritoItem`, validación de stock, procesamiento con datos de envío y pago (tarjeta no persistida) |
| **Perfil** | `PerfilController`, historial de pedidos, actualización de email y contraseña |
| **Novedades y noticias** | `NovedadesController`, `NoticiaAdminController`, `NovedadAdminController`, `Noticia`, `TarjetaNovedad`, paginación y detalle por apartado |
| **Inicio** | `IndexLibrosController`, `IndexLibrosService` (carrusel, recomendaciones, estilo favorito, colecciones desde BD) |
| **Errores** | `CustomErrorController`, plantillas 403, 404, 500 |

---

## Sprint 1 – Core / MVP

*Objetivo: Todo lo esencial para que el sistema arranque de forma segura y usable (autenticación, CRUDs principales, configuración base).*

---

### Historia 1  
**Como** desarrollador/DevOps, **quiero** que la aplicación use variables de entorno o perfiles para la conexión a base de datos (URL, usuario, contraseña), **para** poder desplegar en distintos entornos sin hardcodear credenciales.

**Criterios de Aceptación:**  
1. Las propiedades `spring.datasource.url`, `username` y `password` se resuelven desde variables de entorno o desde `application-{profile}.properties`.  
2. Existe al menos un perfil `dev` (por defecto) y documentación de las variables requeridas en README o en `docs`.  
3. La aplicación arranca correctamente con dichas variables definidas y falla con un mensaje claro si faltan en un perfil no-dev.

---

### Historia 2  
**Como** usuario no registrado, **quiero** poder registrarme con nombre, apellidos, documento, teléfono, género, fecha de nacimiento, email y contraseña, **para** acceder al catálogo y realizar compras asociadas a mi cuenta.

**Criterios de Aceptación:**  
1. El formulario de registro valida todos los campos (incl. email único, DNI único, contraseña con mayúscula, minúscula, número y carácter especial, y edad ≥18 con `@MayorDeEdad`).  
2. Tras registro exitoso se redirige a la página de login con un mensaje de confirmación; el usuario recibe rol VISOR por defecto.  
3. Si el usuario ya está autenticado, al intentar acceder a `/registro` se redirige al inicio.

---

### Historia 3  
**Como** usuario registrado, **quiero** iniciar sesión con mi email y contraseña y ser redirigido al inicio, **para** acceder a carrito persistente, perfil e historial de pedidos.

**Criterios de Aceptación:**  
1. El login usa la página personalizada `/auth/login`, procesa en `/auth/login-process` y en caso de error redirige a `/auth/login?error=true`.  
2. Tras login correcto se redirige a `/`; si el usuario ya estaba autenticado y accede a `/auth/login`, se redirige al inicio.  
3. Las contraseñas se almacenan encriptadas con BCrypt y la autenticación se realiza contra `UsuarioRepository.findByEmail`.

---

### Historia 4  
**Como** administrador (rol PUBLICADOR), **quiero** que las rutas `/libros/**` y `/admin/**` estén protegidas y solo accesibles para mi rol, **para** que solo personal autorizado gestione catálogo y contenido.

**Criterios de Aceptación:**  
1. `SecurityConfig` restringe `/libros/**` y `/admin/**` con `hasRole("PUBLICADOR")`; un usuario VISOR recibe 403 al acceder.  
2. El navbar o fragmentos Thymeleaf muestran enlaces de administración solo cuando el usuario tiene rol PUBLICADOR.  
3. Un intento de acceso no autorizado muestra la página de error 403 configurada en `CustomErrorController`.

---

### Historia 5  
**Como** administrador (PUBLICADOR), **quiero** crear, editar y eliminar libros (título, autor, categoría, precio, imagen, sinopsis, género, stock, novedad), **para** mantener el catálogo actualizado.

**Criterios de Aceptación:**  
1. Existen rutas GET `/libros/nuevo`, GET `/libros/editar/{id}` y POST `/libros/guardar` con validación (`@Valid` Libro); los errores de validación devuelven el formulario con mensajes.  
2. POST `/libros/eliminar` con `libroId` elimina el libro; si falla (p. ej. por restricciones de integridad), se muestra mensaje en flash y se redirige al catálogo.  
3. Los campos obligatorios y reglas del modelo `Libro` (precio > 0, stock ≥ 0, sinopsis longitud, etc.) se cumplen en backend.

---

### Historia 6  
**Como** visitante o usuario, **quiero** ver el catálogo de libros con paginación y filtrar por categoría o por búsqueda por título, **para** encontrar rápidamente los libros que me interesan.

**Criterios de Aceptación:**  
1. La ruta `/catalogo` muestra libros paginados (parámetros `page` y `size`); se muestran categorías y conteos por categoría.  
2. Con parámetro `categoria` se listan solo libros de esa categoría; con `busqueda` se filtra por título; sin ellos se listan todos.  
3. Desde el catálogo se puede acceder al detalle de un libro en `/libro/{id}`; si el id no existe se responde 404.

---

### Historia 7  
**Como** visitante o usuario, **quiero** añadir libros al carrito (por sesión), ver el resumen, actualizar cantidades, eliminar ítems y vaciar el carrito, **para** preparar mi compra sin necesidad de estar logueado.

**Criterios de Aceptación:**  
1. Se puede agregar al carrito vía POST (p. ej. `/carrito/agregar`) con libroId, titulo, precio, imagen; el carrito se identifica por `sessionId`; no se puede superar el stock disponible.  
2. La vista `/carrito` muestra ítems, subtotal, IGV y total; existen acciones para actualizar cantidad, eliminar ítem y limpiar carrito, con redirección y mensajes flash en caso de error.  
3. El endpoint GET `/api/carrito/count` devuelve el número total de ítems del carrito para mostrar el contador en la barra de navegación.

---

### Historia 8  
**Como** usuario o visitante, **quiero** completar la compra desde el carrito ingresando datos de envío (nombre, teléfono, dirección) y datos de pago (tarjeta, vencimiento, CVV), **para** recibir un pedido confirmado y que se descuente el stock.

**Criterios de Aceptación:**  
1. Al procesar el pedido se validan los campos del modelo `Pedido` (nombre, teléfono 9 dígitos empezando en 9, dirección; tarjeta 16 dígitos, CVV 3–4 dígitos, fecha vencimiento no pasada).  
2. Se valida el stock de todos los ítems del carrito antes de confirmar; si falta stock se muestra mensaje de error y no se persiste el pedido.  
3. Tras confirmación se guarda el pedido (con o sin usuario asociado según sesión), se limpia el carrito de la sesión y se muestra mensaje de éxito con ID de pedido.

---

### Historia 9  
**Como** usuario autenticado, **quiero** ver mi perfil y mi historial de pedidos, y poder actualizar mi email y contraseña, **para** mantener mis datos correctos y revisar mis compras.

**Criterios de Aceptación:**  
1. La ruta `/perfil` (protegida) muestra los datos del usuario autenticado y la lista de pedidos ordenados por fecha descendente.  
2. Existe un formulario o flujo para actualizar email y/o contraseña; si se cambia la contraseña se usa `PasswordEncoder` y se persiste.  
3. Si el usuario cambia su email, tras guardar se cierra sesión y se redirige al login para volver a identificarse con el nuevo email.

---

### Historia 10  
**Como** visitante, **quiero** ver la página de inicio con secciones de libros (carrusel, recomendaciones, estilo favorito, colecciones) y acceder a novedades y detalle de noticias por apartado, **para** descubrir contenido sin necesidad de login.

**Criterios de Aceptación:**  
1. La ruta `/` muestra el índice con datos proporcionados por `IndexLibrosService` (carrusel, recomendaciones, estilo favorito, colecciones) basados en libros de la BD.  
2. La ruta `/novedades` muestra tarjetas de novedad paginadas; `/detalle/{apartado}` muestra las noticias del apartado.  
3. Las rutas `/`, `/novedades` y `/detalle/**` son accesibles sin autenticación según `SecurityConfig`.

---

## Sprint 2 – Funcionalidades avanzadas, reportes y UX

*Objetivo: Mejoras de experiencia, reportes, validaciones complejas y funcionalidades que extienden el MVP.*

---

### Historia 11  
**Como** administrador (PUBLICADOR), **quiero** crear, editar y eliminar tarjetas de novedad (título, descripción, imagen, apartado) y asociar un libro opcional, **para** organizar las secciones de novedades en la portada.

**Criterios de Aceptación:**  
1. Existen rutas en `/admin/novedades` para listar/crear/editar/eliminar `TarjetaNovedad` con validación de apartado único (y mensaje si el apartado ya existe).  
2. En el formulario de tarjeta se puede seleccionar un libro opcional (relación con `Libro`); al guardar se persiste la relación.  
3. Tras eliminar una tarjeta se redirige a `/novedades` con mensaje flash de éxito o error según corresponda.

---

### Historia 12  
**Como** administrador (PUBLICADOR), **quiero** crear, editar y eliminar noticias asociadas a una tarjeta de novedad (título, descripción, contenido, imagen, apartado), **para** publicar contenido actualizado en cada sección de novedades.

**Criterios de Aceptación:**  
1. Existen rutas en `/admin/noticias` para nuevo/editar/guardar/eliminar; al guardar se asocia la noticia a la tarjeta seleccionada y se copia el apartado para la URL.  
2. Si la tarjeta no existe o hay error, se muestra mensaje en flash y se redirige al formulario correspondiente sin perder datos críticos.  
3. Tras eliminar una noticia se redirige al detalle del apartado (`/detalle/{apartado}`) o a lista de noticias según diseño acordado.

---

### Historia 13  
**Como** usuario o administrador, **quiero** que las páginas de error 404, 403 y 500 muestren una vista amigable con mensaje y opción de volver al inicio, **para** no ver pantallas en blanco o stack traces en producción.

**Criterios de Aceptación:**  
1. `CustomErrorController` en `/error` devuelve las plantillas `error/404`, `error/403` y `error/500` según el código de estado.  
2. Cada plantilla muestra un mensaje claro y un enlace o botón para regresar al inicio (`/`).  
3. En entorno de producción no se exponen detalles técnicos del error en la página 500 (configuración de `server.error` si aplica).

---

### Historia 14  
**Como** administrador, **quiero** un panel o página de resumen que enlace de forma centralizada a la gestión de libros, noticias y tarjetas de novedad, **para** acceder rápido a todas las tareas de administración.

**Criterios de Aceptación:**  
1. Existe una ruta protegida (p. ej. `/admin` o `/admin/panel`) accesible solo con rol PUBLICADOR.  
2. La página muestra enlaces o tarjetas hacia: listado/alta de libros, gestión de noticias y gestión de tarjetas de novedad.  
3. El navbar o menú de administración incluye un enlace a este panel cuando el usuario es PUBLICADOR.

---

### Historia 15  
**Como** administrador, **quiero** un reporte o vista que liste los pedidos realizados (fecha, usuario o sesión, total, estado si aplica), **para** tener visibilidad de las ventas y poder atender consultas.

**Criterios de Aceptación:**  
1. Existe una ruta protegida (p. ej. `/admin/pedidos`) que lista los pedidos con paginación, mostrando al menos fecha, identificador (id), total y si está asociado a un usuario.  
2. Se puede filtrar por rango de fechas (opcional) o por usuario (opcional); los criterios se aplican en el servicio o repositorio.  
3. Solo usuarios con rol PUBLICADOR pueden acceder; el resto recibe 403.

---

### Historia 16  
**Como** usuario, **quiero** que al agregar un libro al carrito o al actualizar la cantidad se muestre un mensaje claro en la interfaz (éxito o error de stock) sin depender solo del refresh de la página, **para** tener feedback inmediato.

**Criterios de Aceptación:**  
1. La acción de agregar al carrito (p. ej. vía JavaScript contra el endpoint existente) muestra un mensaje de éxito o error (toast, alerta o texto en pantalla) según la respuesta del servidor.  
2. Al actualizar cantidad en la página del carrito, si el servidor devuelve error (stock insuficiente), se muestra el mensaje en la misma vista (flash o mensaje en línea).  
3. El contador del carrito en el navbar se actualiza tras agregar un ítem (usando `/api/carrito/count` o equivalente).

---

### Historia 17  
**Como** usuario, **quiero** que en la página de detalle del libro se muestre la disponibilidad (stock) y que no se pueda añadir al carrito si el stock es cero, **para** evitar frustración al intentar comprar algo no disponible.

**Criterios de Aceptación:**  
1. La vista de detalle (`/libro/{id}`) muestra información de stock (ej. “En stock” / “Solo X unidades” / “Agotado”).  
2. Si el stock es 0, el botón “Añadir al carrito” está deshabilitado o oculto, con un texto que indique que no hay disponibilidad.  
3. Si por race condition el usuario intenta agregar con stock 0 (p. ej. desde catálogo), el backend responde con error y el front muestra el mensaje correspondiente.

---

### Historia 18  
**Como** desarrollador/Product Owner, **quiero** que la configuración de seguridad permita habilitar CSRF en producción (o documentar por qué está deshabilitado), **para** reducir el riesgo de ataques CSRF en entornos reales.

**Criterios de Aceptación:**  
1. Existe documentación (README o docs) que indica el estado de CSRF (deshabilitado en dev vs habilitado en prod) y cómo enviar el token en formularios si está habilitado.  
2. Si se habilita CSRF para un perfil (p. ej. `prod`), los formularios de login, registro, carrito y admin incluyen el token CSRF y las peticiones POST son aceptadas.  
3. Las peticiones GET que solo leen datos no requieren token; las que modifican datos (POST/PUT/DELETE) lo requieren cuando CSRF está activo.

---

### Historia 19  
**Como** usuario, **quiero** que el formulario de registro y el de actualización de perfil validen en el cliente (JavaScript) los mismos criterios que el servidor (email, DNI, contraseña, edad), **para** obtener feedback inmediato sin enviar el formulario.

**Criterios de Aceptación:**  
1. En la página de registro hay validación en JavaScript para formato de email, longitud y formato de DNI, y reglas de contraseña (mayúscula, minúscula, número, carácter especial); se muestra mensaje de error junto al campo.  
2. La fecha de nacimiento se valida en cliente para asegurar que el usuario sea mayor de 18 años (o se muestra un mensaje acorde).  
3. El servidor mantiene las validaciones actuales; los mensajes de error del servidor se muestran en la misma vista sin perder los datos ya ingresados (excepto contraseña).

---

### Historia 20  
**Como** administrador, **quiero** que al eliminar un libro se compruebe si existe en ítems de pedidos o carritos activos, y se muestre un mensaje claro si no se puede eliminar por integridad, **para** evitar errores de base de datos y dar una acción alternativa (p. ej. desactivar o editar stock).

**Criterios de Aceptación:**  
1. Antes de eliminar un libro se verifica si está referenciado en `CarritoItem` o en ítems de `Pedido`; si lo está, no se elimina y se devuelve mensaje explicativo en flash.  
2. El mensaje al usuario indica que el libro no se puede eliminar porque está en pedidos o carritos y sugiere una acción alternativa si está definida (ej. poner stock a 0).  
3. Si no hay referencias, la eliminación se ejecuta y se redirige al catálogo con mensaje de éxito.

---

## Notas de implementación

- **Sprint 1** asume que la BD y los datos iniciales (`data.sql`) están correctos y que los roles PUBLICADOR y VISOR existen.  
- **Sprint 2** puede requerir pequeños ajustes en `SecurityConfig` (p. ej. ruta `/admin` o `/admin/pedidos`) y nuevas plantillas o fragmentos.  
- Las historias 18 y 20 son técnicas/seguridad y refuerzo de reglas de negocio; pueden priorizarse según criterio del Product Owner.

---

*Documento generado a partir del análisis del codebase del proyecto LecturaSana (branch feature/reestructuracion-proyecto).*
