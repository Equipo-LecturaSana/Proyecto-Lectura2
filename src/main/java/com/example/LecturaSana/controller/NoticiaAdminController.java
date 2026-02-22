package com.example.LecturaSana.controller;
import com.example.LecturaSana.model.Noticia;
import com.example.LecturaSana.model.TarjetaNovedad;
import com.example.LecturaSana.service.NoticiaService;
import com.example.LecturaSana.service.NovedadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/noticias")
public class NoticiaAdminController {
    private final NoticiaService ns; private final NovedadService vs;
    public NoticiaAdminController(NoticiaService ns, NovedadService vs) { this.ns = ns; this.vs = vs; }

    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("noticia", new Noticia());
        m.addAttribute("tarjetas", vs.obtenerNovedades());
        return "admin/form-noticia";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam(value="id", required=false) Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("contenido") String contenido,
            @RequestParam("imagen") String imagen,
            @RequestParam("tarjetaId") Long tarjetaId, // Recibimos el ID
            RedirectAttributes attr) {
        try {
            TarjetaNovedad tarjeta = vs.obtenerPorId(tarjetaId);
            if (tarjeta == null) throw new Exception("Categoría no existe");

            Noticia n = (id != null) ? ns.obtenerPorId(id) : new Noticia();
            n.setTitulo(titulo); n.setDescripcion(descripcion); n.setContenido(contenido); n.setImagen(imagen);
            
            // VINCULACIÓN MANUAL SEGURA
            n.setTarjetaNovedad(tarjeta);
            n.setApartado(tarjeta.getApartado()); // Copiar el slug para la URL

            ns.guardarNoticia(n);
            attr.addFlashAttribute("mensajeExito", "Noticia guardada");
            return "redirect:/detalle/" + tarjeta.getApartado();
        } catch (Exception e) {
            attr.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/admin/noticias/nuevo";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Noticia n = ns.obtenerPorId(id);
        model.addAttribute("noticia", n);
        model.addAttribute("tarjetas", vs.obtenerNovedades());
        return "admin/form-noticia";
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes attr) {
        Noticia n = ns.obtenerPorId(id);
        String redir = (n != null) ? n.getApartado() : "";
        if(n!=null) ns.eliminarNoticia(id);
        return "redirect:/detalle/" + redir;
    }
}