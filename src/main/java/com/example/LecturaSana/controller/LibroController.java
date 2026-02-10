package com.example.LecturaSana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.service.LibroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    // 1. Mostrar formulario para AÑADIR
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("tituloPagina", "Añadir Nuevo Libro");
        return "admin/form-libro"; // Asegúrate de que esta plantilla exista
    }

    // 2. Mostrar formulario para EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Libro libro = libroService.obtenerPorId(id)
                .orElse(null);
        
        if (libro == null) {
            return "redirect:/catalogo";
        }
        
        model.addAttribute("libro", libro);
        model.addAttribute("tituloPagina", "Editar Libro");
        return "admin/form-libro";
    }

    // 3. Procesar GUARDADO
    @PostMapping("/guardar")
    public String guardarLibro(@Valid @ModelAttribute("libro") Libro libro, 
                             BindingResult bindingResult, 
                             RedirectAttributes redirectAttributes,
                             Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("tituloPagina", libro.getId() == null ? "Añadir Nuevo Libro" : "Editar Libro");
            return "admin/form-libro";
        }

        libroService.guardarLibro(libro);
        redirectAttributes.addFlashAttribute("mensajeExito", "¡Libro guardado exitosamente!");
        return "redirect:/catalogo";
    }

    // 4. Procesar ELIMINACIÓN
    @PostMapping("/eliminar")
    public String eliminarLibro(@RequestParam("libroId") Long id, RedirectAttributes redirectAttributes) {
        try {
            libroService.eliminarLibro(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Libro eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede eliminar el libro (puede estar en pedidos).");
        }
        return "redirect:/catalogo";
    }
}