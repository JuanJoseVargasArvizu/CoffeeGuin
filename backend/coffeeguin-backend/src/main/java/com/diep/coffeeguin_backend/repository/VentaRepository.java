package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
	List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	List<Venta> findByFechaGreaterThanEqual(LocalDateTime inicio);

	List<Venta> findByFechaLessThanEqual(LocalDateTime fin);
}