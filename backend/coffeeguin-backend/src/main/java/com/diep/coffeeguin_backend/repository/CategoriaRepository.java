package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
