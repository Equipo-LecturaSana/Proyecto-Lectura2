package com.example.LecturaSana.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.LecturaSana.model.Rol; // Importar Rol
import com.example.LecturaSana.model.Usuario;
import com.example.LecturaSana.repository.RolRepository; // Importar el nuevo repositorio
import com.example.LecturaSana.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // <--- ¡INYECCIÓN NUEVA!

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Guardar o actualizar un usuario
    public Usuario guardar(Usuario usuario) {
        // 1. Encriptar contraseña
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);

        // 2. --- ¡CORRECCIÓN AQUÍ! ASIGNAR ROL POR DEFECTO ---
        if (usuario.getRol() == null) {
            // Buscamos el rol "VISOR" en la base de datos
            Rol rolDefecto = rolRepository.findByNombre("VISOR")
                    .orElse(null);
            
            // Si por alguna razón no lo encuentra por nombre, intentamos por ID (2)
            // (Esto es una seguridad extra por si el nombre en BD fuera minúscula 'visor')
            if (rolDefecto == null) {
                 rolDefecto = rolRepository.findById(2L).orElse(null);
            }

            // Asignamos el rol al usuario
            usuario.setRol(rolDefecto);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizarPassword(Usuario usuario, String nuevaPassword) {
        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
        usuario.setPassword(passwordEncriptada);
        return usuarioRepository.save(usuario);
    }

    // ... (El resto de tus métodos: buscarPorId, listarTodos, etc. siguen IGUAL)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existePorNumeroDocumento(String numeroDocumento) {
        return usuarioRepository.existsByNumeroDocumento(numeroDocumento);
    }
}