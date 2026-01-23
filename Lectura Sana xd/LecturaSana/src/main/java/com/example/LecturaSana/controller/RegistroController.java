package com.example.LecturaSana.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.LecturaSana.model.Usuario;
import com.example.LecturaSana.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        
        // --- ¡VALIDACIÓN NUEVA! ---
        // Si ya estás logueado, no deberías poder registrarte de nuevo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/"; 
        }
        
        model.addAttribute("usuario", new Usuario());
        return "registro"; 
    }

    @PostMapping("/procesar_registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "registro"; 
        }

        if (usuarioService.existePorEmail(usuario.getEmail())) {
            model.addAttribute("error", "El correo electrónico ya está registrado.");
            return "registro";
        }
        
        if (usuarioService.existePorNumeroDocumento(usuario.getNumeroDocumento())) {
            model.addAttribute("error", "El número de documento ya está registrado.");
            return "registro";
        }

        usuarioService.guardar(usuario);
        return "redirect:/auth/login?registroExitoso=true";
    }
}