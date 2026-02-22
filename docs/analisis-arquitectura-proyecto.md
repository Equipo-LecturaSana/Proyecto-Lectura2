# Análisis de Arquitectura – Proyecto LecturaSana

**Rol:** Arquitecto de Software  
**Objetivo:** Analizar la estructura real del proyecto (backend y frontend) para basar las 20 Historias de Usuario en lo ya construido.

---

## 1. Aclaración importante: Stack real del proyecto

Tras revisar la estructura de carpetas y archivos del repositorio, el proyecto **no** está dividido en:

- Frontend en **React**
- Backend en **Spring Boot** (como dos aplicaciones separadas)

**Stack actual:**

| Capa | Tecnología | Ubicación |
|------|------------|-----------|
| **Backend** | Spring Boot 3.5.6 (Java 21) | `src/main/java/` |
| **Vista / “Frontend”** | **Thymeleaf** (HTML server-side) + Bootstrap 5 + JS vanilla | `src/main/resources/templates/`, `static/css/`, `static/js/` |
| **Persistencia** | Spring Data JPA + MySQL | Repositorios + `application.properties` |

Es decir: **una aplicación monolítica** donde Spring Boot sirve tanto la API (algunos endpoints devuelven JSON, p. ej. carrito) como las páginas HTML generadas con Thymeleaf. No hay carpeta `frontend/` ni proyecto React (no existe `package.json` con dependencias React, ni Redux ni Context API).

Si en el futuro se desea migrar a **React + Spring Boot** (front y back separados), este análisis sirve como base del backend actual; las historias de usuario se podrían adaptar añadiendo historias para “API REST para el frontend React” y “componentes React”.

---

## 2. Entidades ya creadas en el backend

Todas están en `src/main/java/com/example/LecturaSana/model/` y mapeadas con JPA (salvo `IndexLibros`, que es un DTO).

| Entidad | Tabla / Uso | Descripción breve |
|---------|-----------------|--------------------|
| **Usuario** | `usuarios` | Usuario del sistema; implementa `UserDetails`; relación con `Rol` y `Pedido`. |
| **Rol** | `roles` | Roles (ej. PUBLICADOR, VISOR). |
| **Libro** | `libros` | Catálogo: título, autor, categoría, precio, imagen, sinopsis, género, stock, novedad. |
| **CarritoItem** | `carrito_items` | Ítems del carrito; identificados por `sessionId` y `libroId`. |
| **Pedido** | `pedidos` | Cabecera de pedido: fecha, subtotal, IGV, total, datos de envío; relación con `Usuario` (opcional) e ítems. |
| **Noticia** | `Noticia` (tabla `Noticia`) | Noticia dentro de un apartado: título, descripción, contenido, imagen; asociada a `TarjetaNovedad`. |
| **TarjetaNovedad** | `tarjetaNovedad` | Sección de novedades: título, descripción, imagen, apartado (slug); puede tener un `Libro` asociado. |
| **IndexLibros** | (no es entidad) | DTO con listas: carrusel, recomendaciones, estiloFavorito, colecciones. |

**Resumen:** 7 entidades JPA + 1 DTO. No existe entidad “Book” en inglés; el equivalente es **Libro**. No existe “User” en inglés; el equivalente es **Usuario**.

---

## 3. Controladores y endpoints operativos

Resumen de controladores y rutas que existen hoy (y que devuelven vista Thymeleaf o JSON).

### 3.1 IndexLibrosController

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/` | Página de inicio (index.html) con secciones de libros. |

### 3.2 LoginController (`/auth`)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/auth/login` | Formulario de login (login.html). |

*(El procesamiento del login es `/auth/login-process` por Spring Security, no por un controlador propio.)*

### 3.3 RegistroController

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/registro` | Formulario de registro (registro.html). |
| POST | `/procesar_registro` | Procesa registro y redirige a login. |

### 3.4 CatalogoController

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/catalogo` | Catálogo paginado con filtro por categoría y búsqueda (catalogo.html). |
| GET | `/libro/{id}` | Detalle de un libro (detalle-libro.html). |

### 3.5 CarritoController

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/carrito` | Página del carrito (carrito.html). |
| POST | `/carrito/agregar` | **JSON** – Agrega ítem al carrito. |
| GET | `/api/carrito/count` | **JSON** – Cantidad de ítems en el carrito. |
| POST | `/carrito/procesar` | Procesa pedido y redirige a `/carrito`. |
| POST | `/carrito/actualizar-cantidad` | Actualiza cantidad y redirige a `/carrito`. |
| POST | `/carrito/eliminar/{id}` | Elimina ítem y redirige a `/carrito`. |
| POST | `/carrito/limpiar` | Vacía el carrito y redirige a `/carrito`. |

### 3.6 PerfilController (`/perfil`)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/perfil` | Página de perfil + historial de pedidos (perfil.html). |
| POST | `/perfil/actualizar` | Actualiza email y/o contraseña; redirige a perfil o logout. |

### 3.7 NovedadesController (público)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/novedades` | Lista de tarjetas de novedad paginada (novedades.html). |
| GET | `/detalle/{apartado}` | Noticias del apartado (noticias.html). |

### 3.8 LibroController – Admin (`/libros`)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/libros/nuevo` | Formulario nuevo libro (admin/form-libro.html). |
| GET | `/libros/editar/{id}` | Formulario editar libro. |
| POST | `/libros/guardar` | Guarda o actualiza libro; redirige a catálogo. |
| POST | `/libros/eliminar` | Elimina libro por `libroId`; redirige a catálogo. |

### 3.9 NoticiaAdminController (`/admin/noticias`)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/admin/noticias/nuevo` | Formulario nueva noticia. |
| GET | `/admin/noticias/editar/{id}` | Formulario editar noticia. |
| POST | `/admin/noticias/guardar` | Guarda noticia. |
| POST | `/admin/noticias/eliminar/{id}` | Elimina noticia. |

### 3.10 NovedadAdminController (`/admin/novedades`)

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/admin/novedades/nuevo` | Formulario nueva tarjeta de novedad. |
| GET | `/admin/novedades/editar/{id}` | Formulario editar tarjeta. |
| POST | `/admin/novedades/guardar` | Guarda tarjeta. |
| POST | `/admin/novedades/eliminar/{id}` | Elimina tarjeta. |

### 3.11 CustomErrorController

| Método | Ruta | Respuesta |
|--------|------|-----------|
| GET | `/error` | Páginas 403, 404 o 500 según código de estado. |

**Resumen:** Los endpoints operativos son los listados; el resto de rutas (login-process, logout) las gestiona Spring Security según `SecurityConfig`.

---

## 4. “Frontend”: componentes de interfaz existentes

Al no haber React, la “interfaz” está en **plantillas Thymeleaf** y **fragmentos**. Equivalencia aproximada con conceptos de componentes:

| Concepto típico en React | En este proyecto |
|--------------------------|-------------------|
| Layout / Shell | `fragmentos/navbar.html`, `fragmentos/footer.html` |
| Página Home | `index.html` |
| Página Login | `login.html` |
| Página Registro | `registro.html` |
| Catálogo / Listado | `catalogo.html` |
| Detalle de producto | `detalle-libro.html` |
| Carrito | `carrito.html` |
| Perfil de usuario | `perfil.html` |
| Novedades (listado) | `novedades.html` |
| Noticias por apartado | `noticias.html` |
| Formularios admin | `admin/form-libro.html`, `admin/form-noticia.html`, `admin/form-novedad.html` |
| Páginas de error | `error/403.html`, `error/404.html`, `error/500.html` |

**CSS:** `static/css/style.css`, `style2.css`, `style3.css`, `style4.css` (y Bootstrap vía CDN o estático).  
**JS:** `static/js/funcioncarrito.js`, `funcioncatalogo.js`, `funcionregistro.js` (vanilla JS, sin framework).

No hay: Navbar “component” en React, ni Catalog “component”, ni rutas cliente (React Router). La navegación es por enlaces HTML que llevan a nuevas peticiones al servidor.

---

## 5. Cómo se maneja el “estado”

En una app React se hablaría de Context API, Redux o props. En esta app:

| Tipo de estado | Dónde se maneja |
|----------------|------------------|
| **Sesión / usuario autenticado** | Spring Security (`SecurityContextHolder`); sesión HTTP en servidor. |
| **Carrito** | Servidor: tabla `carrito_items` asociada a `sessionId`; el cliente no guarda estado del carrito, solo llama a `/carrito/agregar` y `/api/carrito/count`. |
| **Datos de pantalla (listas, detalle)** | Modelo de Thymeleaf: el controlador pone en `Model` los datos y la plantilla los muestra; cada solicitud es “fresh”. |
| **Formularios (validación, errores)** | Servidor: `BindingResult` y flash attributes; en cliente, scripts en `funcionregistro.js` (y otros) para validación básica o UX. |

No hay: Context API, Redux, ni estado global en un framework frontend. El “estado” es básicamente **sesión en servidor + modelo por petición + algo de JS en el cliente**.

---

## 6. Resumen para las Historias de Usuario

- **Entidades:** Usuario, Rol, Libro, CarritoItem, Pedido, Noticia, TarjetaNovedad (+ DTO IndexLibros). No hay “User” ni “Book” en inglés en el código.
- **Endpoints:** Los listados en la sección 3 están operativos (vistas Thymeleaf y 2 endpoints JSON para carrito).
- **Interfaz:** Thymeleaf (navbar, footer, páginas de catálogo, carrito, perfil, admin, errores) + CSS + JS vanilla.
- **Estado:** Sesión (Spring Security), carrito por `sessionId` en BD, modelo por petición, sin Context/Redux.

Las **20 historias de usuario** (10 Sprint 1 + 10 Sprint 2) que ya se generaron en `docs/historias-usuario-tecnicas.md` están basadas en este stack real (Spring Boot + Thymeleaf). Si se confirma que el objetivo es **seguir con este stack**, se pueden usar esas historias tal cual o ajustar redacción. Si el objetivo es **migrar a React + Spring Boot**, se puede:

1. Mantener las historias de negocio actuales.
2. Añadir/adaptar historias para: “Exponer API REST para frontend” y “Implementar en React: Navbar, Catalog, Carrito, etc.” y “Estado global en frontend (Context/Redux)”.

Indica si quieres que las 20 historias se dejen como están (Thymeleaf) o que se reescriban/amplíen asumiendo una futura migración a React + Spring Boot.
