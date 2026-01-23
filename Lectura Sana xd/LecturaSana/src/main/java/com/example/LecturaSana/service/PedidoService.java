package com.example.LecturaSana.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LibroService libroService;

    public PedidoService(PedidoRepository pedidoRepository, LibroService libroService) {
        this.pedidoRepository = pedidoRepository;
        this.libroService = libroService;
    }

    public List<Pedido> getPedidos() {
        return pedidoRepository.findAll(); 
    }

    @Transactional
    public void guardarPedido(Pedido pedido) {
        // Guardar el pedido primero
        pedidoRepository.save(pedido);
        
        // Actualizar el stock de cada libro en el pedido
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            for (CarritoItem item : pedido.getItems()) {
                try {
                    libroService.reducirStock(item.getLibroId(), item.getCantidad());
                } catch (Exception e) {
                    System.err.println("Error al actualizar stock para libro ID " + item.getLibroId() + ": " + e.getMessage());
                    // Continuamos con los demás items incluso si hay error en uno
                }
            }
        }
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}