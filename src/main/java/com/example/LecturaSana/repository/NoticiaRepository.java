
package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.Noticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia,Long>{
    List<Noticia> findByApartado(String apartado);

    List<Noticia> findByTituloContainingIgnoreCase(String titulo);

    List<Noticia> findByDescripcionContainingIgnoreCase(String descripcion);
    
    List<Noticia> findByContenidoContainingIgnoreCase(String contenido);

    List<Noticia> findByApartadoContainingIgnoreCase(String apartado);

    // Consultas personalizadas
    @Query("SELECT DISTINCT l.apartado FROM Noticia l WHERE l.apartado IS NOT NULL ORDER BY l.apartado")
    List<String> findDistinctApartados();

    @Query("SELECT l.apartado, COUNT(l) FROM Noticia l WHERE l.apartado IS NOT NULL GROUP BY l.apartado ORDER BY l.apartado")
    List<Object[]> countNoticiasPorApartado();

    // Búsqueda en múltiples campos
    @Query("SELECT l FROM Noticia l WHERE "
            + "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR "
            + "LOWER(l.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) OR "
            + "LOWER(l.apartado) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Noticia> buscarEnTodosLosCampos(@Param("termino") String termino);
}


