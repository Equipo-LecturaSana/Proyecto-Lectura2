package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método para buscar un rol por su nombre (ej: "VISOR")
    Optional<Rol> findByNombre(String nombre);
}