package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
}
