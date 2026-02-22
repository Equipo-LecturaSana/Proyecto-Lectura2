package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.model.Usuario; // <-- Importar Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // <-- Importar List

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    /**
     * ¡ESTE ES EL MÉTODO QUE FALTABA!
     * Busca todos los pedidos de un usuario y los ordena 
     * del más reciente al más antiguo.
     */
    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);
    
}