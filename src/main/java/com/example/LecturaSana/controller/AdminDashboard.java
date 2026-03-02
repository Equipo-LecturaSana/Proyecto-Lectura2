package com.example.LecturaSana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboard {

    @GetMapping("/panel")
    public String panel() {
        return "adminPanel"; 
        // templates/admin/panel.html
    }

    @GetMapping("/libros")
    public String gestionarLibros() {
        return "redirect:/libros"; 
        // reutiliza tu vista actual de libros
    }

    @GetMapping("/novedades")
    public String gestionarNovedades() {
        return "redirect:/novedades";
    }

    @GetMapping("/noticias")
    public String gestionarNoticias() {
        return "redirect:/noticias";
    }
}