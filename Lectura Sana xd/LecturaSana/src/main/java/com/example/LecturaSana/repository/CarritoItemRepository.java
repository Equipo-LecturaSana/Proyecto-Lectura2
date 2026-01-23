package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findBySessionId(String sessionId);
    
    CarritoItem findBySessionIdAndLibroId(String sessionId, Long libroId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM CarritoItem c WHERE c.sessionId = ?1 AND c.libroId = ?2")
    void deleteBySessionIdAndLibroId(String sessionId, Long libroId);
    
    @Transactional
    void deleteBySessionId(String sessionId);
    
    @Transactional
    void deleteById(Long id);
}