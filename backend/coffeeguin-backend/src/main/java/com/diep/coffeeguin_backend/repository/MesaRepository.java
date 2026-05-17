package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Mesa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
    @Query("SELECT m FROM Mesa m WHERE m.estado = :estado")
    List<Mesa> findByEstado(String estado);
}
