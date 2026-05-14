package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
}