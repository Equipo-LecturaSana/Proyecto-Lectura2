package com.example.LecturaSana.service;
import com.example.LecturaSana.model.IndexLibros;
import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.repository.IndexLibrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndexLibrosServiceImpl implements IndexLibrosService {

    @Autowired
    private IndexLibrosRepository indexLibrosRepository;

    @Override
    public IndexLibros obtenerSeccionesIndex() {
        List<Libro> todos = indexLibrosRepository.findAll();

        if (todos.isEmpty()) {
            return new IndexLibros(Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList());
        }

        // Mezclamos todo aleatoriamente
        Collections.shuffle(todos);

        // --- Carrusel (3 libros) ---
        List<Libro> carrusel = todos.stream()
                .limit(3)
                .collect(Collectors.toList());

        // --- Recomendaciones (3 libros aleatorios distintos) ---
        List<Libro> recomendaciones = obtenerAleatorios(todos, 3);

        // --- Estilo Favorito (6 géneros distintos o hasta 6 libros aleatorios) ---
        List<Libro> estiloFavorito = obtener6GenerosUnicos(todos);

        // --- Colecciones (3 aleatorios distintos) ---
        List<Libro> colecciones = obtenerAleatorios(todos, 3);

        return new IndexLibros(carrusel, recomendaciones, estiloFavorito, colecciones);
    }

    /**
     * Retorna una lista de libros aleatorios sin repetir
     */
    private List<Libro> obtenerAleatorios(List<Libro> lista, int cantidad) {
        List<Libro> copia = new ArrayList<>(lista);
        Collections.shuffle(copia);
        return copia.stream().limit(cantidad).collect(Collectors.toList());
    }

    /**
     * Retorna hasta 6 libros con géneros distintos
     */
    private List<Libro> obtener6GenerosUnicos(List<Libro> todos) {
        Map<String, Libro> porGenero = new LinkedHashMap<>();

        // Evitamos duplicar géneros
        for (Libro libro : todos) {
            if (!porGenero.containsKey(libro.getGenero())) {
                porGenero.put(libro.getGenero(), libro);
            }
            if (porGenero.size() >= 6) break;
        }

        // Si hay menos de 6 géneros, completamos con libros aleatorios
        if (porGenero.size() < 6) {
            List<Libro> faltantes = obtenerAleatorios(todos, 6 - porGenero.size());
            for (Libro l : faltantes) {
                if (porGenero.size() >= 6) break;
                porGenero.putIfAbsent(l.getTitulo() + UUID.randomUUID(), l);
            }
        }

        return new ArrayList<>(porGenero.values());
    }
}

