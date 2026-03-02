# Unificación visual UI (estilo GitHub) – Resumen

**Objetivo:** Unificar todas las vistas Thymeleaf al sistema visual de `registro.html` (cards limpias, bordes suaves, inputs claros), manteniendo la paleta de colores del proyecto.

**Restricciones respetadas:** Solo se modificaron `src/main/resources/templates/**` y `src/main/resources/static/css/**`. No se tocaron controladores, servicios, repositorios, rutas, BD ni Jenkinsfile. No se cambiaron textos, lógica de formularios ni nombres de endpoints.

---

## 1. Archivos modificados

### CSS (creado / modificado)

| Archivo | Cambio |
|---------|--------|
| **`static/css/ui-github.css`** | **Nuevo.** Sistema base: variables CSS (colores del proyecto), `.ui-container`, `.ui-card`, `.ui-card-header`, `.ui-input`, `.btn-libreria` / `.ui-btn-primary`, `.ui-btn-outline`, `.ui-section-title`, `.ui-card-style`, estilos globales de `.form-control` / `.form-select` (fondo claro, borde, focus con ring teal). |
| **`static/css/formadmin.css`** | Card admin pasa a fondo blanco y borde sutil (hereda de ui-github). Eliminado fondo gris `#dfdfdf`; solo se mantienen márgenes y `max-width`. |
| **`static/css/style.css`** | `body`: fondo `#f6f8fa`, fuente sistema. |
| **`static/css/style3.css`** | `body`: fondo `#f6f8fa`, fuente sistema. |
| **`static/css/style4.css`** | `body`: fondo `#f6f8fa`, fuente sistema. |

### Templates

| Archivo | Cambios |
|---------|--------|
| **`index.html`** | Enlace a `ui-github.css`. `main` con `.ui-container`. Títulos de sección con `.ui-section-title`. Cards de recomendaciones/colecciones con `.ui-card-style`. Secciones “Estilo Favorito” y “Catálogo completo” con `.ui-card`. Botones principales (Ver Detalles, Ver Colección, Ir al Catálogo) con `.btn-libreria`. |
| **`login.html`** | `ui-github.css`. Contenedor con `.ui-container`. Card con `.ui-card`. Inputs sin `input-group` (labels + `.form-control`). Botón enviar: `.btn-libreria` y texto “Iniciar sesión”. |
| **`registro.html`** | Enlace a `ui-github.css`. Contenedor con `.ui-container`. Resto igual (ya usaba `.register-card` y `.btn-libreria`; ui-github unifica el aspecto). |
| **`catalogo.html`** | `ui-github.css`. Contenedor principal con `.ui-container`. Stats y filtros con `.ui-card` / `.ui-card-style`. Cards de libros con `.ui-card-style`. |
| **`detalle-libro.html`** | `ui-github.css`. `main` con `.ui-container`. Card principal con `.ui-card`. Botón “Añadir al Carrito” con `.btn-libreria`. “Volver al Catálogo” con `.ui-btn-outline`. |
| **`novedades.html`** | `ui-github.css`. Contenedores con `.ui-container`. Cards de novedad con `.ui-card-style`. Botón “VER DETALLES” con `.btn-libreria`. |
| **`noticias.html`** | `ui-github.css`. Contenedores con `.ui-container`. Cada noticia con `.ui-card`. Botón “Volver a Novedades” con `.btn-libreria`. |
| **`carrito.html`** | `ui-github.css`. `main` con `.ui-container`. Título con `.ui-section-title`. Card resumen con `.ui-card`. Botón “Pagar” con `.btn-libreria`. |
| **`perfil.html`** | `ui-github.css`. Contenedor con `.ui-container`. Cards con `.ui-card`. Formulario de actualización con `.ui-card`. Botón “Guardar cambios” con `.btn-libreria`. **Corrección de estructura:** eliminado `<li class="nav-item">` erróneo que envolvía la columna “Historial de Compras”; reemplazado por `<div class="col-lg-7">`. |
| **`adminPanel.html`** | `ui-github.css`. `.dashboard-wrap` con `.ui-container`. |
| **`admin/form-noticia.html`** | Enlace a `ui-github.css`. Card y inputs ya usan `.card-github` / `.github-input` (definidos en ui-github). |
| **`admin/form-novedad.html`** | Enlace a `ui-github.css`. |
| **`admin/form-libro.html`** | Enlace a `ui-github.css`. |
| **`error/404.html`** | `ui-github.css`. Contenedor con `.ui-container`. Botón “Volver al Inicio” con `.btn-libreria`. |
| **`error/403.html`** | `ui-github.css`. Quitado `login.css`. Contenedor con `.ui-container`. Botón con `.btn-libreria`. |
| **`error/500.html`** | `ui-github.css`. Contenedor con `.ui-container`. Botón con `.btn-libreria`. |

---

## 2. Resumen de qué cambió y por qué

### Sistema visual unificado (`ui-github.css`)

- **Variables CSS:** Colores existentes del proyecto: `--ui-primary` (#2F4F4F), `--ui-primary-hover` (#1a2e2e), `--ui-border` (#d0d7de), `--ui-bg-page` (#f6f8fa), `--ui-focus-color` (#4ca1af), etc. Sin colores nuevos de marca.
- **Contenedor:** `.ui-container` para máximo ancho y padding uniforme en el contenido.
- **Cards:** `.ui-card` y `.ui-card-style`: fondo blanco, borde `#d0d7de`, `border-radius` 6px, sombra suave (estilo registro).
- **Inputs:** `.form-control` y `.form-select` con fondo `#f6f8fa`, borde gris y focus con ring teal (accesible y coherente con el proyecto).
- **Botones:** `.btn-libreria` (y alias `.ui-btn-primary`) como primario; `.ui-btn-outline` / `.github-btn-outline` para secundarios. Mismo padding, altura y radius.
- **Títulos de sección:** `.ui-section-title` para tipografía y espaciado consistentes.

### Páginas

- **Index, catálogo, novedades, noticias, carrito, perfil:** Mismo “sistema”: contenedor centrado, cards con borde sutil y sombra, botones principales con `.btn-libreria`, inputs con el estilo unificado cuando están dentro de cards o del contenedor.
- **Login:** Ajustado a card tipo registro, inputs con label (sin input-group) y botón `.btn-libreria`.
- **Registro:** Solo se añade `ui-github.css` y `.ui-container`; el resto ya coincidía.
- **Admin (panel y formularios):** Cards blancas con borde sutil; formularios usan los mismos inputs y botones que el resto del sitio.
- **Páginas de error (403, 404, 500):** Mismo fondo, contenedor y botón “Volver” con `.btn-libreria`.

### Corrección de layout

- **perfil.html:** Eliminado el `<li class="nav-item">` que envolvía incorrectamente la segunda columna (Historial de Compras), sustituido por un `<div class="col-lg-7">` para que el layout sea correcto.

---

## 3. Lo que no se ha modificado

- Navbar y footer (fragmentos y estilos propios).
- Imágenes y datos (solo UI).
- Textos, rutas, acciones de formularios y nombres de endpoints.
- Controladores, servicios, repositorios, BD, Jenkinsfile.
- Lógica de negocio.

---

## 4. Verificación rápida

Revisar en el navegador:

1. **Index:** Fondo gris claro, cards blancas con borde, botones “Ver Detalles” / “Ver Colección” / “Ir al Catálogo” con el mismo estilo oscuro.
2. **Login / Registro:** Misma card y mismos inputs (fondo claro, borde, focus teal).
3. **Catálogo / Detalle libro:** Contenedor y cards con el mismo estilo; botón “Añadir al Carrito” / “Volver” coherentes.
4. **Novedades / Noticias:** Cards y botones unificados.
5. **Carrito:** Resumen en card blanca; botón “Pagar” con `.btn-libreria`.
6. **Perfil:** Dos columnas bien alineadas (card perfil + card historial); formulario en card; botón “Guardar cambios” y “Cerrar sesión” visibles.
7. **Admin:** Formularios con card blanca e inputs estilo GitHub.
8. **403 / 404 / 500:** Fondo y botón “Volver al Inicio” con el mismo estilo.
