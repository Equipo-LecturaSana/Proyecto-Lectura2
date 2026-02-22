package com.example.LecturaSana.repository;

import com.example.LecturaSana.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexLibrosRepository extends JpaRepository<Libro, Long> {
}

