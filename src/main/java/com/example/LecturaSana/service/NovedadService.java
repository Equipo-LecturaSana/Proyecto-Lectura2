package com.example.LecturaSana.service;

import com.example.LecturaSana.model.TarjetaNovedad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.LecturaSana.repository.TarjetaNovedadRepository;
import java.util.List;
import java.util.Optional;

@Service
public class NovedadService {

    private final TarjetaNovedadRepository repo;

    public NovedadService(TarjetaNovedadRepository repo) {
        this.repo = repo;
    }

    public List<TarjetaNovedad> obtenerNovedades() {
        return repo.findAll();
    }

    public Page<TarjetaNovedad> obtenerNovedadesPaginadas(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public boolean existeApartado(String apartado, Long idExcluir) {
        Optional<TarjetaNovedad> encontrada = repo.findByApartado(apartado);
        if (encontrada.isPresent()) {
            if (idExcluir != null && encontrada.get().getId().equals(idExcluir)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public TarjetaNovedad guardar(TarjetaNovedad t) {
        return repo.save(t);
    }

    public TarjetaNovedad obtenerPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
