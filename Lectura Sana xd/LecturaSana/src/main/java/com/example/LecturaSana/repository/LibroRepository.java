package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.Libro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    // --- MÉTODOS CON PAGINACIÓN (CLAVE PARA QUE CARGUE) ---
    Page<Libro> findAll(Pageable pageable);

    Page<Libro> findByCategoria(String categoria, Pageable pageable);

    Page<Libro> findByNovedadTrue(Pageable pageable);

    Page<Libro> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // --- Métodos auxiliares ---
    @Query("SELECT DISTINCT l.categoria FROM Libro l WHERE l.categoria IS NOT NULL ORDER BY l.categoria")
    List<String> findDistinctCategorias();

    @Query("SELECT l.categoria, COUNT(l) FROM Libro l WHERE l.categoria IS NOT NULL GROUP BY l.categoria ORDER BY l.categoria")
    List<Object[]> countLibrosPorCategoria();

    @Query("SELECT MAX(l.id) FROM Libro l")
    Long findMaxId();

    // Para estadísticas
    @Query("SELECT AVG(l.precio) FROM Libro l")
    Double findPrecioPromedio();

    @Query("SELECT l FROM Libro l WHERE l.stock < :stockMinimo")
    List<Libro> findLibrosStockBajo(@Param("stockMinimo") Integer stockMinimo);

    @Query("SELECT COUNT(l) FROM Libro l WHERE l.novedad = true")
    Long countNovedades();
}
