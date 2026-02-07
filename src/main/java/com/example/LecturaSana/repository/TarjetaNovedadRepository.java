package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.TarjetaNovedad;
import org.springframework.data.domain.Page; 
import org.springframework.data.domain.Pageable; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaNovedadRepository extends JpaRepository<TarjetaNovedad, Long> {
    
    // Paginación para la vista pública
    Page<TarjetaNovedad> findAll(Pageable pageable);
    
    // Búsqueda exacta para validación de duplicados (Optional es más seguro)
    Optional<TarjetaNovedad> findByApartado(String apartado);

    // Búsquedas auxiliares
    List<TarjetaNovedad> findByTituloContainingIgnoreCase(String titulo);
    List<TarjetaNovedad> findByDescripcionContainingIgnoreCase(String descripcion);
    List<TarjetaNovedad> findByApartadoContainingIgnoreCase(String apartado);

    @Query("SELECT l FROM TarjetaNovedad l WHERE "
            + "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termino, '%')) OR "
            + "LOWER(l.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) OR "
            + "LOWER(l.apartado) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<TarjetaNovedad> buscarEnTodosLosCampos(@Param("termino") String termino);
}