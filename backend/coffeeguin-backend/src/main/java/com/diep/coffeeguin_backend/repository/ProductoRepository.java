package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	@Query("""
			SELECT DISTINCT p FROM Producto p
			LEFT JOIN FETCH p.lineasReceta lr
			LEFT JOIN FETCH lr.ingrediente
			""")
	List<Producto> findAllWithReceta();

	@Query("""
			SELECT DISTINCT p FROM Producto p
			LEFT JOIN FETCH p.lineasReceta lr
			LEFT JOIN FETCH lr.ingrediente
			WHERE p.categoria.id = :catId
			""")
	List<Producto> findByCategoriaIdWithReceta(@Param("catId") Long catId);

	@Query("""
			SELECT DISTINCT p FROM Producto p
			LEFT JOIN FETCH p.lineasReceta lr
			LEFT JOIN FETCH lr.ingrediente
			WHERE p.tipo <> 'ingrediente'
			   OR EXISTS (SELECT 1 FROM Ingrediente i WHERE i.id = p.id AND i.stockActual > 0)
			""")
	List<Producto> findAllDisponiblesWithReceta();

	@Query("""
			SELECT DISTINCT p FROM Producto p
			LEFT JOIN FETCH p.lineasReceta lr
			LEFT JOIN FETCH lr.ingrediente
			WHERE p.categoria.id = :catId
			  AND (p.tipo <> 'ingrediente'
			   OR EXISTS (SELECT 1 FROM Ingrediente i WHERE i.id = p.id AND i.stockActual > 0))
			""")
	List<Producto> findByCategoriaDisponiblesWithReceta(@Param("catId") Long catId);

	@Query("""
			SELECT DISTINCT p FROM Producto p
			LEFT JOIN FETCH p.lineasReceta lr
			LEFT JOIN FETCH lr.ingrediente
			WHERE p.id = :id
			""")
	Optional<Producto> findByIdWithReceta(@Param("id") Long id);
}
