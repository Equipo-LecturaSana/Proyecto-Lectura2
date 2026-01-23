package com.example.LecturaSana.service;

import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.repository.CarritoItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final LibroService libroService;

    public CarritoService(CarritoItemRepository repo, LibroService ls) {
        this.carritoItemRepository = repo;
        this.libroService = ls;
    }

    public List<CarritoItem> obtenerCarritoPorSession(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId);
    }

    @Transactional
    public void agregarAlCarrito(String sessionId, Long libroId, String titulo, Double precio, String imagen) throws Exception {
        Libro libro = libroService.obtenerPorId(libroId).orElseThrow(() -> new Exception("Libro no existe"));
        if (libro.getStock() <= 0) {
            throw new Exception("Agotado");
        }

        CarritoItem item = carritoItemRepository.findBySessionIdAndLibroId(sessionId, libroId);
        int cantidad = (item != null) ? item.getCantidad() : 0;

        if (cantidad + 1 > libro.getStock()) {
            throw new Exception("Stock insuficiente");
        }

        if (item != null) {
            item.setCantidad(cantidad + 1);
            carritoItemRepository.save(item);
        } else {
            carritoItemRepository.save(new CarritoItem(libroId, titulo, precio, 1, imagen, sessionId));
        }
    }

    @Transactional
    public void actualizarCantidad(Long itemId, Integer cantidad) throws Exception {
        if (cantidad <= 0) {
            carritoItemRepository.deleteById(itemId);
            return;
        }
        CarritoItem item = carritoItemRepository.findById(itemId).orElseThrow();
        Libro libro = libroService.obtenerPorId(item.getLibroId()).orElseThrow();
        if (cantidad > libro.getStock()) {
            throw new Exception("Stock insuficiente");
        }
        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
    }

    public void validarStockCarritoCompleto(String sessionId) throws Exception {
        List<CarritoItem> carrito = obtenerCarritoPorSession(sessionId);
        if (carrito.isEmpty()) {
            throw new Exception("Carrito vacío");
        }
        for (CarritoItem item : carrito) {
            Libro libro = libroService.obtenerPorId(item.getLibroId()).orElseThrow();
            if (item.getCantidad() > libro.getStock()) {
                throw new Exception("Stock insuficiente: " + item.getTitulo());
            }
        }
    }

    public void eliminarItemPorId(Long id) {
        carritoItemRepository.deleteById(id);
    }

    @Transactional
    public void limpiarCarrito(String sessionId) {
        carritoItemRepository.deleteBySessionId(sessionId);
    }

    public double calcularTotal(String sessionId) {
        double total = obtenerCarritoPorSession(sessionId).stream()
                .mapToDouble(item -> item.getPrecio() * item.getCantidad())
                .sum();
        // Redondear a 2 decimales
        return Math.round(total * 100.0) / 100.0;
    }
}
