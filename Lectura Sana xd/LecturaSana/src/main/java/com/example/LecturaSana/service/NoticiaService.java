package com.example.LecturaSana.service;

import com.example.LecturaSana.model.Noticia;
import org.springframework.stereotype.Service;
import com.example.LecturaSana.repository.NoticiaRepository;
import java.util.List;

@Service
public class NoticiaService {

    private final NoticiaRepository repo;

    public NoticiaService(NoticiaRepository repo) {
        this.repo = repo;
    }

    public List<Noticia> obtenerPorApartado(String apartado) {
        return repo.findByApartado(apartado);
    }

    public Noticia obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Noticia guardarNoticia(Noticia n) {
        return repo.save(n);
    }

    public void eliminarNoticia(Long id) {
        repo.deleteById(id);
    }
}
