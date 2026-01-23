package com.example.LecturaSana.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.LecturaSana.model.*;
import com.example.LecturaSana.service.*;

@Controller
public class NovedadesController {

    private final NovedadService ns;
    private final NoticiaService notiService;

    public NovedadesController(NovedadService ns, NoticiaService notiService) {
        this.ns = ns;
        this.notiService = notiService;
    }

    @GetMapping("/novedades")
    public String novedades(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size, Model model) {
        Page<TarjetaNovedad> pagina = ns.obtenerNovedadesPaginadas(PageRequest.of(page, size));
        model.addAttribute("paginaNovedades", pagina);
        return "novedades";
    }

    @GetMapping("/detalle/{apartado}")
    public String detalle(@PathVariable String apartado, Model model) {
        List<Noticia> noticias = notiService.obtenerPorApartado(apartado);
        model.addAttribute("noticias", noticias);
        model.addAttribute("apartado", apartado);
        return "noticias";
    }
}
