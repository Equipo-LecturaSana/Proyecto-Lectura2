package com.example.LecturaSana.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.LecturaSana.model.Usuario;

@Controller
@RequestMapping("/auth")
public class LoginController {

    @GetMapping("/login")
    public String mostrarLoginForm(Model model) {
        
        // --- ¡VALIDACIÓN NUEVA! ---
        // Si el usuario ya está logueado, lo echamos al inicio
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/"; 
        }
        
        model.addAttribute("usuario", new Usuario());
        return "login"; 
    }
}