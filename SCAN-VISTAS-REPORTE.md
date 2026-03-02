# Reporte SCAN GENERAL – Vistas tras merge (feature/mejora-visual-novedades → main)

**Fecha:** 2 marzo 2026  
**Alcance:** `src/main/resources/templates`, `src/main/resources/static`. Sin cambios en lógica de negocio ni Jenkinsfile.

---

## 1. Resumen de hallazgos

| # | Archivo afectado | Tipo de problema | Evidencia (línea o snippet) | Impacto | Fix propuesto | Estado |
|---|------------------|-------------------|------------------------------|---------|---------------|--------|
| 1 | `fragmentos/footer.html` | **Fragment** | No existía `th:fragment="footer"` en el `<footer>`. | **500** en todas las páginas que usan `th:replace="fragmentos/footer :: footer"` (index, login, catálogo, novedades, noticias, carrito, perfil, errores 403/404/500, etc.). | Añadir `th:fragment="footer"` y `xmlns:th` al elemento `<footer>`. | ✅ Aplicado |
| 2 | `index.html` | **Ruta** | `th:href="@{'/catalogo/detalle/' + ${libro.id}}"` (aprox. línea 69). | Enlace "Ver Detalles" en Recomendaciones lleva a 404; el controlador expone `/libro/{id}`, no `/catalogo/detalle/{id}`. | Cambiar a `th:href="@{'/libro/' + ${libro.id}}"`. | ✅ Aplicado |
| 3 | `index.html` | **Ruta** | `th:href="@{'/catalogo/coleccion/' + ${libro.categoria}}"` (aprox. línea 103). | Enlace "Ver Colección" da 404; no existe `/catalogo/coleccion/`. El catálogo filtra por `?categoria=`. | Cambiar a `th:href="@{'/catalogo?categoria=' + ${libro.categoria}}"`. | ✅ Aplicado |
| 4 | `admin/form-noticia.html` | **Ruta / estático** | `href="/novedades"` y `action="/admin/noticias/guardar"` sin Thymeleaf. | En despliegue con context path o proxy, enlaces/acción pueden fallar. | Usar `th:href="@{/novedades}"` y `th:action="@{/admin/noticias/guardar}"`. | ✅ Aplicado |
| 5 | `login.html` | **Layout / HTML** | `<script src="...bootstrap.bundle.min.js">` sin cierre `</script>`. | HTML inválido; posibles fallos de carga de script en algunos navegadores. | Cerrar con `</script>`. | ✅ Aplicado |
| 6 | `registro.html` | **Layout** | Solo incluye navbar; no incluye `fragmentos/footer`. | Inconsistencia visual respecto al resto de páginas públicas. | Opcional: añadir `<div th:replace="~{fragmentos/footer :: footer}"></div>` antes del cierre de `body`. | ⚪ No aplicado (opcional) |
| 7 | `data/noticias.json` | **Estático (datos)** | Rutas con espacio: `"/IMG/imagen novedades/..."`. En `static` la carpeta es `imagen_novedades`. | Imágenes de noticias pueden devolver 404. | Corregir en JSON a `"/IMG/imagen_novedades/..."` (underscore). No tocado (datos, no solo front). | ⚪ Reportado |
| 8 | `data.sql` / `libros.json` | **Estático (datos)** | Referencias a `/IMG/Imagen_Index/` o `imagen Index/`; en `static/IMG` solo existe `imagen_novedades`. | Imágenes de libros en index/catálogo pueden 404. | Crear carpeta y recursos o unificar rutas en datos. No tocado (datos). | ⚪ Reportado |

---

## 2. Comprobaciones realizadas (sin errores detectados)

- **Fragmentos navbar:** Todos los templates que usan navbar referencian `fragmentos/navbar :: navbar`; el fragmento está definido en `fragmentos/navbar.html`. Sintaxis mixta `fragmentos/...` vs `~{fragmentos/...}` es válida en Thymeleaf 3.
- **Rutas de controladores vs vistas:**  
  - `/` → index, `/catalogo` → catalogo, `/libro/{id}` → detalle-libro, `/novedades` → novedades, `/detalle/{apartado}` → noticias, `/auth/login` → login, `/registro` → registro, `/carrito` → carrito, `/perfil` → perfil, `/admin/panel` → adminPanel, formularios admin coherentes con controladores.
- **CSS/JS estáticos:** Referencias en templates usan `th:href="@{/css/...}"` y `th:src="@{/js/...}"`; archivos existen en `static/css` y `static/js` (style.css, style2–4, navbar, footer, formadmin, dashboard, registro, login; funcioncatalogo, funcionregistro, funcioncarrito).
- **Novedades:** `novedades.html` enlaza a `/detalle/${novedad.apartado}`, alineado con `NovedadesController` (`/detalle/{apartado}`). Redirecciones en `NoticiaAdminController` a `/detalle/...` son correctas.

---

## 3. Fixes aplicados (resumen)

1. **fragmentos/footer.html**  
   - Añadido `th:fragment="footer"` y `xmlns:th="http://www.thymeleaf.org"` al elemento `<footer>`.

2. **index.html**  
   - "Ver Detalles" (Recomendaciones): `/catalogo/detalle/` → `/libro/`.  
   - "Ver Colección" (Colecciones): `/catalogo/coleccion/` → `/catalogo?categoria=`.

3. **admin/form-noticia.html**  
   - Botón Cancelar: `href="/novedades"` → `th:href="@{/novedades}"`.  
   - Formulario: `action="/admin/noticias/guardar"` → `th:action="@{/admin/noticias/guardar}"`.

4. **login.html**  
   - Script de Bootstrap cerrado correctamente con `</script>`.

---

## 4. Checklist de verificación manual (páginas clave)

Tras desplegar o ejecutar la app, revisar:

- [ ] **Inicio** (`/`)  
  - Navbar y footer se ven.  
  - "Ver Detalles" en Recomendaciones abre detalle del libro (no 404).  
  - "Ver Colección" en Colecciones abre catálogo filtrado por categoría (no 404).  
  - Carrusel y estilos correctos.

- [ ] **Catálogo** (`/catalogo`)  
  - Navbar y footer.  
  - Filtros y paginación.  
  - Enlace a detalle de libro (`/libro/{id}`) y botón "Ver todos".

- [ ] **Novedades** (`/novedades`)  
  - Navbar y footer.  
  - Tarjetas con imagen; "VER DETALLES" lleva a `/detalle/{apartado}` (página de noticias).  
  - Paginación si hay más de una página.

- [ ] **Noticias** (`/detalle/comics` o otro apartado)  
  - Navbar y footer.  
  - Carrusel y listado de noticias (imágenes pueden 404 si no se corrige `noticias.json`).

- [ ] **Login** (`/auth/login`)  
  - Navbar y footer.  
  - Formulario funciona; no hay errores en consola por script sin cerrar.

- [ ] **Registro** (`/registro`)  
  - Navbar (footer no añadido; opcional).

- [ ] **Carrito** (`/carrito`)  
  - Navbar y footer.  
  - Tabla y botones (actualizar, PayPal, etc.).

- [ ] **Perfil** (`/perfil`)  
  - Navbar y footer.  
  - Contenido según rol.

- [ ] **Admin**  
  - Panel (`/admin/panel`): navbar.  
  - Form noticia (`/admin/noticias/nuevo` o editar): botón "Cancelar" vuelve a `/novedades`; envío del formulario guarda correctamente.  
  - Form novedad / form libro: comportamiento y estilos correctos.

- [ ] **Páginas de error**  
  - `/ruta-inexistente` → 404 con navbar y footer.  
  - Si aplica, 403 y 500 con layout correcto.

- [ ] **Recursos estáticos**  
  - En DevTools (pestaña Network): sin 404 en `/css/*`, `/js/*`.  
  - Imágenes en novedades/noticias: si hay 404, revisar `data/noticias.json` y rutas en BD (`/IMG/imagen_novedades/` vs `imagen novedades`).

---

## 5. Notas

- **Jenkinsfile:** No modificado (solo front y referencias en templates).  
- **Lógica de negocio:** Sin cambios en controladores ni servicios.  
- **Datos (data.sql, noticias.json, libros.json):** Rutas de imágenes con espacios o carpetas inexistentes (`Imagen_Index`, `imagen novedades`) quedan como punto a revisar con negocio/datos; no se han modificado en este scan.
