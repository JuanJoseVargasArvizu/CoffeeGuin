package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer> {
}
