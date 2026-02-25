-- 1. INSERTAR ROLES
INSERT INTO roles (id, nombre) VALUES (1, 'PUBLICADOR') ON DUPLICATE KEY UPDATE id=id;
INSERT INTO roles (id, nombre) VALUES (2, 'VISOR') ON DUPLICATE KEY UPDATE id=id;

-- 2. INSERTAR TARJETAS DE NOVEDAD (Categorías)
INSERT INTO tarjeta_novedad (id, titulo, descripcion, imagen, apartado) VALUES
(1, 'Cómics', 'Noticias sobre cómics y sus autores', '/IMG/imagen_novedades/comics.jpg', 'comics'),
(2, 'Cine', 'Últimas novedades del cine y sus películas', '/IMG/imagen_novedades/cine.jpg', 'cine'),
(3, 'Mangas', 'Todo sobre el manga y anime japonés', '/IMG/imagen_novedades/mangas.jpg', 'mangas'),
(4, 'Novelas', 'Clásicos y nuevos lanzamientos de literatura', '/IMG/imagen_novedades/novelas.jpg', 'novelas'),
(5, 'Biografías', 'Historias reales que inspiran al mundo', '/IMG/imagen_novedades/biografias.jpg', 'biografias'),
(6, 'Fantasía', 'Mundos mágicos y aventuras épicas', '/IMG/imagen_novedades/fantasia.jpg', 'fantasia')
ON DUPLICATE KEY UPDATE titulo=titulo;

-- 3. INSERTAR NOTICIAS (Ejemplos)
INSERT INTO noticia (id, apartado, titulo, descripcion, contenido, imagen, tarjeta_id) VALUES
(1,'comics','MUERE STAN LEE','El famoso escritor...','Stan Lee, creador de iconos...','/IMG/imagen_novedades/ncomics1.jpg',1),
(2,'cine','EL SEÑOR DE LOS ANILLOS','La trilogía dirigida por...','Su narrativa atemporal...','/IMG/imagen_novedades/ncine1.jpg',2)
ON DUPLICATE KEY UPDATE titulo=titulo;

-- 4. INSERTAR LIBROS (¡CORREGIDO CON IDs!)
-- Al poner el ID (1, 2, 3...), si el libro ya existe, no se duplica.
INSERT INTO libros (id, titulo, autor, categoria, precio, imagen, sinopsis, genero, stock, novedad) VALUES
(1, 'Cazadores de Sombras', 'Cassandra Clare', 'accion', 19.99, '/IMG/Imagen_Index/descarga.jpg', 'Una historia oscura de amor y demonios...', 'Fantasía Urbana', 50, TRUE),
(2, 'El Peso del Silencio', 'Laura Gallego', 'accion', 22.50, '/IMG/Imagen_Index/descarga (2).jpg', 'Una conmovedora historia sobre una familia...', 'Drama Familiar', 45, FALSE),
(3, 'Bajo el Mismo Cielo', 'Isabel Keats', 'romance', 17.95, '/IMG/Imagen_Index/descarga (1).jpg', 'Dos almas destinadas a encontrarse...', 'Romance Contemporáneo', 60, TRUE),
(4, 'Nexus Horizon', 'Daniel Cruz', 'ciencia-ficcion', 24.75, '/IMG/Imagen_Index/descarga (3).jpg', 'En un futuro donde la humanidad...', 'Ciencia Ficción', 35, TRUE),
(5, 'El Heredero de los Reinos', 'Elena Montoya', 'fantasia', 21.99, '/IMG/Imagen_index/descarga (6).jpg', 'Un joven descubre que es el heredero...', 'Fantasía Épica', 40, TRUE),
(6, 'Susurros en la Oscuridad', 'Miguel Ángel Sánchez', 'terror', 18.50, '/IMG/Imagen_Index/descarga (4).jpg', 'Una casa abandonada con un pasado...', 'Terror Psicológico', 30, FALSE),
(7, 'Vida Entre Pinceles', 'Clara Ruiz', 'biografia', 26.80, '/IMG/Imagen_Index/vida-pinceles.jpg', 'La fascinante vida de la pintora...', 'Biografía Artística', 25, TRUE),
(8, 'Operación Cero', 'Carlos Mendoza', 'accion', 20.45, '/IMG/Imagen_Index/operacion-cero.jpg', 'Un agente secreto debe evitar...', 'Thriller de Espionaje', 55, FALSE),
(9, 'Las Huellas del Ayer', 'Ana Torres', 'accion', 23.99, '/IMG/Imagen_Index/huellas-ayer.jpg', 'Tres generaciones de mujeres...', 'Drama Familiar', 48, TRUE)
ON DUPLICATE KEY UPDATE titulo=titulo;


-- 5. INSERTAR USUARIO ADMIN (¡IMPORTANTE: USAR EL EMAIL COMO CLAVE PARA ACTUALIZAR!)
-- Esto asegura que si el admin ya existe, se le ponga la contraseña correcta y el rol correcto.
INSERT INTO usuarios (nombre, apellidos, tipo_documento, numero_documento, telefono, genero, cumpleanos, email, password, activo, rol_id)
VALUES (
    'Admin', 
    'LecturaSana', 
    'DNI', 
    '12345678', 
    '987654321', 
    'Otro', 
    '2000-01-01', 
    'admin@lecturasana.com', 
    '$2a$10$kMZb/F.TT176gLikAyWVnuANaFQxBrp6vOOaTHFfbEeQaJwNhbNyu',  -- Hash de "admin123"
    TRUE, 
    1 -- Rol PUBLICADOR
)

ON DUPLICATE KEY UPDATE 
    password='$2a$10$kMZb/F.TT176gLikAyWVnuANaFQxBrp6vOOaTHFfbEeQaJwNhbNyu', 
    rol_id=1, 
    activo=TRUE;

    INSERT INTO usuarios (nombre, apellidos, tipo_documento, numero_documento, telefono, genero, cumpleanos, email, password, activo, rol_id)
VALUES (
    'UsuarioPrueba', 
    'Usuario1', 
    'DNI', 
    '12312312', 
    '987654321', 
    'Otro', 
    '2000-01-01', 
    'usuario1@lecturasana.com', 
    '$2a$10$kMZb/F.TT176gLikAyWVnuANaFQxBrp6vOOaTHFfbEeQaJwNhbNyu',  -- Hash de "admin123"
    TRUE, 
    2 -- Rol PUBLICADOR
)

ON DUPLICATE KEY UPDATE 
    password='$2a$10$kMZb/F.TT176gLikAyWVnuANaFQxBrp6vOOaTHFfbEeQaJwNhbNyu', 
    rol_id=2, 
    activo=TRUE;