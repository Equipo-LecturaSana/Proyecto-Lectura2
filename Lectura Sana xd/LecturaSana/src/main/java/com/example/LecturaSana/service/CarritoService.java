package com.example.LecturaSana.service;

import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Libro;
import com.example.LecturaSana.repository.CarritoItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final LibroService libroService;

    private static final BigDecimal IGV_RATE = new BigDecimal("0.18");
    private static final int MAX_CANTIDAD_POR_ITEM = 50;

    public CarritoService(CarritoItemRepository repo, LibroService ls) {
        this.carritoItemRepository = repo;
        this.libroService = ls;
    }

    /**
     * Obtiene todos los items del carrito para una sesión específica
     */
    public List<CarritoItem> obtenerCarritoPorSession(String sessionId) {
        validarSessionId(sessionId);
        return carritoItemRepository.findBySessionId(sessionId);
    }

    /**
     * Agrega un libro al carrito o incrementa su cantidad si ya existe
     */
    @Transactional
    public void agregarAlCarrito(String sessionId, Long libroId, String titulo, BigDecimal precio, String imagen) {
        validarSessionId(sessionId);
        validarDatosLibro(libroId, titulo, precio);

        Libro libro = libroService.obtenerPorId(libroId)
            .orElseThrow(() -> new IllegalArgumentException("El libro con ID " + libroId + " no existe"));

        if (libro.getStock() <= 0) {
            throw new IllegalStateException("El libro '" + titulo + "' está agotado");
        }

        CarritoItem item = carritoItemRepository.findBySessionIdAndLibroId(sessionId, libroId);
        int cantidadActual = (item != null) ? item.getCantidad() : 0;
        int nuevaCantidad = cantidadActual + 1;

        if (nuevaCantidad > libro.getStock()) {
            throw new IllegalStateException("Stock insuficiente para '" + titulo + "'. Disponible: " + libro.getStock());
        }

        if (nuevaCantidad > MAX_CANTIDAD_POR_ITEM) {
            throw new IllegalStateException("No puedes agregar más de " + MAX_CANTIDAD_POR_ITEM + " unidades del mismo libro");
        }

        if (item != null) {
            item.incrementarCantidad();
            carritoItemRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem(libroId, titulo, precio, 1, imagen, sessionId);
            carritoItemRepository.save(nuevoItem);
        }
    }

    /**
     * Actualiza la cantidad de un item en el carrito
     */
    @Transactional
    public void actualizarCantidad(Long itemId, Integer cantidad) {
        if (itemId == null) {
            throw new IllegalArgumentException("El ID del item no puede ser nulo");
        }

        if (cantidad != null && cantidad <= 0) {
            carritoItemRepository.deleteById(itemId);
            return;
        }

        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula");
        }

        if (cantidad > MAX_CANTIDAD_POR_ITEM) {
            throw new IllegalArgumentException("La cantidad no puede exceder " + MAX_CANTIDAD_POR_ITEM + " unidades");
        }

        CarritoItem item = carritoItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item de carrito no encontrado"));

        Libro libro = libroService.obtenerPorId(item.getLibroId())
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (cantidad > libro.getStock()) {
            throw new IllegalStateException(
                "Stock insuficiente para '" + item.getTitulo() + "'. " +
                "Solicitado: " + cantidad + ", Disponible: " + libro.getStock()
            );
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);
    }

    /**
     * Valida que todos los items del carrito tengan stock suficiente
     */
    @Transactional(readOnly = true)
    public void validarStockCarritoCompleto(String sessionId) {
        validarSessionId(sessionId);
        List<CarritoItem> carrito = obtenerCarritoPorSession(sessionId);

        if (carrito.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío");
        }

        for (CarritoItem item : carrito) {
            Libro libro = libroService.obtenerPorId(item.getLibroId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado: " + item.getTitulo()));

            if (item.getCantidad() > libro.getStock()) {
                throw new IllegalStateException(
                    "Stock insuficiente para '" + item.getTitulo() + "'. " +
                    "Cantidad en carrito: " + item.getCantidad() + ", Disponible: " + libro.getStock()
                );
            }
        }
    }

    /**
     * Elimina un item específico del carrito
     */
    @Transactional
    public void eliminarItemPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del item no puede ser nulo");
        }
        if (!carritoItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item de carrito no encontrado");
        }
        carritoItemRepository.deleteById(id);
    }

    /**
     * Limpia completamente el carrito de una sesión
     */
    @Transactional
    public void limpiarCarrito(String sessionId) {
        validarSessionId(sessionId);
        carritoItemRepository.deleteBySessionId(sessionId);
    }

    /**
     * Calcula el subtotal del carrito (sin IGV)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotal(String sessionId) {
        validarSessionId(sessionId);
        return obtenerCarritoPorSession(sessionId).stream()
                .map(CarritoItem::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el IGV del carrito
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularIgv(String sessionId) {
        BigDecimal subtotal = calcularSubtotal(sessionId);
        return subtotal.multiply(IGV_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el total del carrito (subtotal + IGV)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(String sessionId) {
        BigDecimal subtotal = calcularSubtotal(sessionId);
        BigDecimal igv = calcularIgv(sessionId);
        return subtotal.add(igv).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Cuenta la cantidad total de items en el carrito
     */
    @Transactional(readOnly = true)
    public int contarItemsEnCarrito(String sessionId) {
        validarSessionId(sessionId);
        return obtenerCarritoPorSession(sessionId).stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }

    /**
     * Verifica si el carrito está vacío
     */
    @Transactional(readOnly = true)
    public boolean carritoEstaVacio(String sessionId) {
        validarSessionId(sessionId);
        return obtenerCarritoPorSession(sessionId).isEmpty();
    }

    // Métodos de validación privados

    private void validarSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de sesión no puede estar vacío");
        }
    }

    private void validarDatosLibro(Long libroId, String titulo, BigDecimal precio) {
        if (libroId == null) {
            throw new IllegalArgumentException("El ID del libro no puede ser nulo");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro no puede estar vacío");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio del libro debe ser mayor a cero");
        }
    }
}
