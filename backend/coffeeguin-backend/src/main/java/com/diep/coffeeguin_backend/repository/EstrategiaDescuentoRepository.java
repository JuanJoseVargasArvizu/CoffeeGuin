package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstrategiaDescuentoRepository extends JpaRepository<EstrategiaDescuento, Integer> {
    List<EstrategiaDescuento> findByActivaTrue();
}
