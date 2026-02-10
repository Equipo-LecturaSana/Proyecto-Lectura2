package com.example.LecturaSana.service;

import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.repository.LibroRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    // --- MÉTODOS PAGINADOS ---
    public Page<Libro> obtenerTodos(Pageable pageable) {
        return libroRepository.findAll(pageable);
    }

    public Page<Libro> obtenerPorCategoria(String categoria, Pageable pageable) {
        if (categoria == null || categoria.isEmpty() || categoria.equals("all")) {
            return libroRepository.findAll(pageable);
        }
        return libroRepository.findByCategoria(categoria, pageable);
    }

    public Page<Libro> buscarPorTitulo(String titulo, Pageable pageable) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo, pageable);
    }

    public Page<Libro> obtenerNovedades(Pageable pageable) {
        return libroRepository.findByNovedadTrue(pageable);
    }

    // --- MÉTODOS AUXILIARES (Dropdowns, Stats, Stock) ---
    public List<Libro> obtenerTodosSinPaginar() { // Para el admin
        return libroRepository.findAll();
    }

    public Optional<Libro> obtenerPorId(Long id) {
        return libroRepository.findById(id);
    }

    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }

    // Método crítico para compras
    @Transactional
    public void reducirStock(Long libroId, int cantidad) throws Exception {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new Exception("Libro no encontrado"));

        if (libro.getStock() < cantidad) {
            throw new Exception("Stock insuficiente para: " + libro.getTitulo());
        }
        libro.setStock(libro.getStock() - cantidad);
        libroRepository.save(libro);
    }

    // Estadísticas para el catálogo
    public List<String> obtenerTodasLasCategorias() {
        return libroRepository.findDistinctCategorias();
    }

    public int obtenerTotalLibros() {
        return (int) libroRepository.count();
    }

    public Map<String, Integer> obtenerConteosPorCategoria() {
        List<Object[]> resultados = libroRepository.countLibrosPorCategoria();
        Map<String, Integer> conteos = new HashMap<>();
        for (Object[] res : resultados) {
            conteos.put((String) res[0], ((Long) res[1]).intValue());
        }
        return conteos;
    }
}
