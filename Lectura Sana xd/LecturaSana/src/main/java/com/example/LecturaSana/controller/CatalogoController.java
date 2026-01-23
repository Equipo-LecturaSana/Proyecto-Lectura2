package com.example.LecturaSana.controller;

import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.service.LibroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CatalogoController {

    private final LibroService libroService;

    public CatalogoController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "busqueda", required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Libro> paginaLibros;
        String categoriaSeleccionada = "Todas las categorías";
        String terminoBusqueda = "";

        // Lógica de filtrado
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            paginaLibros = libroService.buscarPorTitulo(busqueda, pageable);
            terminoBusqueda = busqueda;
            categoriaSeleccionada = "Resultados: '" + busqueda + "'";
        } else if (categoria != null && !categoria.isEmpty() && !categoria.equals("all")) {
            paginaLibros = libroService.obtenerPorCategoria(categoria, pageable);
            categoriaSeleccionada = categoria;
        } else {
            paginaLibros = libroService.obtenerTodos(pageable);
        }

        // Cargar datos en el modelo
        model.addAttribute("paginaLibros", paginaLibros); // ¡ESTO ES LO IMPORTANTE!
        model.addAttribute("categorias", libroService.obtenerTodasLasCategorias());
        model.addAttribute("conteosPorCategoria", libroService.obtenerConteosPorCategoria());
        model.addAttribute("coloresCategorias", crearMapaColores());
        model.addAttribute("categoriaSeleccionada", categoriaSeleccionada);
        model.addAttribute("totalLibros", libroService.obtenerTotalLibros());
        model.addAttribute("terminoBusqueda", terminoBusqueda);

        return "catalogo";
    }

    @GetMapping("/libro/{id}")
    public String detalleLibro(@PathVariable("id") Long id, Model model) {
        Libro libro = libroService.obtenerPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado"));
        model.addAttribute("libro", libro);
        model.addAttribute("coloresCategorias", crearMapaColores());
        return "detalle-libro";
    }

    private Map<String, String> crearMapaColores() {
        Map<String, String> colores = new HashMap<>();
        colores.put("accion", "danger");
        colores.put("drama", "primary");
        colores.put("romance", "success");
        colores.put("ciencia-ficcion", "info");
        colores.put("fantasia", "warning");
        colores.put("terror", "dark");
        colores.put("biografia", "secondary");
        colores.put("manga", "success");
        colores.put("comic", "warning");
        return colores;
    }
}
