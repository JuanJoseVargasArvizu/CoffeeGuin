package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import java.util.List;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    @Query("SELECT i FROM Ingrediente i WHERE i.stockActual <= i.umbralAlerta")
    List<Ingrediente> buscarIngredientesConBajoStock();
}
