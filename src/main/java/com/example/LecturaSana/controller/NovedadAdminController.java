package com.example.LecturaSana.controller;

import com.example.LecturaSana.model.*;
import com.example.LecturaSana.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/novedades")
public class NovedadAdminController {
    private final NovedadService ns; private final LibroService ls;
    public NovedadAdminController(NovedadService ns, LibroService ls) { this.ns = ns; this.ls = ls; }

    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("tarjetaNovedad", new TarjetaNovedad());
        m.addAttribute("libros", ls.obtenerTodosSinPaginar());
        return "admin/form-novedad";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("tarjetaNovedad") TarjetaNovedad t, BindingResult res, Model m, RedirectAttributes attr) {
        if (ns.existeApartado(t.getApartado(), t.getId())) res.rejectValue("apartado", "error", "Apartado ya existe");
        if (res.hasErrors()) { m.addAttribute("libros", ls.obtenerTodosSinPaginar()); return "admin/form-novedad"; }
        ns.guardar(t);
        attr.addFlashAttribute("mensajeExito", "Guardado");
        return "redirect:/novedades";
    }
    // ... editar/eliminar similares ...
     @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        TarjetaNovedad tarjeta = ns.obtenerPorId(id);
        if (tarjeta == null) return "redirect:/novedades";
        model.addAttribute("tarjetaNovedad", tarjeta);
        model.addAttribute("libros", ls.obtenerTodosSinPaginar());
        return "admin/form-novedad";
    }
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attr) {
        try { ns.eliminar(id); attr.addFlashAttribute("mensajeExito", "Eliminado"); }
        catch(Exception e) { attr.addFlashAttribute("mensajeError", "Error al eliminar"); }
        return "redirect:/novedades";
    }
}