package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Dictamen;

@Repository
public interface DictamenRepository extends JpaRepository<Dictamen, Long> {
    
    Optional<Dictamen> findByCitaId(Long citaId);
    
    // NUEVO: Método para eliminar dictamen por citaId
    @Modifying
    @Transactional
    void deleteByCitaId(Long citaId);
}