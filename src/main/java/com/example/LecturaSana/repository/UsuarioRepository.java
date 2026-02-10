package com.example.LecturaSana.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.LecturaSana.model.Usuario;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar usuario por email (útil para login)
    Optional<Usuario> findByEmail(String email);

    // Verificar si ya existe un email registrado
    boolean existsByEmail(String email);

    // Verificar si ya existe un número de documento
    boolean existsByNumeroDocumento(String numeroDocumento);
}
