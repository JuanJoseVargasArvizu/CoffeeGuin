package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
}
