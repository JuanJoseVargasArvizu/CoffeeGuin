package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.ProductoReceta;
import com.diep.coffeeguin_backend.model.ProductoRecetaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRecetaRepository extends JpaRepository<ProductoReceta, ProductoRecetaId> {

	/**
	 * Obtiene todas las líneas de receta para un producto específico.
	 * @param productoId ID del producto
	 * @return Lista de líneas de receta (asociaciones con ingredientes)
	 */
	@Query("""
			SELECT pr FROM ProductoReceta pr
			WHERE pr.producto.id = :productoId
			ORDER BY pr.ingrediente.nombre
			""")
	List<ProductoReceta> findByProductoId(@Param("productoId") Long productoId);

	/**
	 * Obtiene todas las recetas que usan un ingrediente específico.
	 * @param ingredienteId ID del ingrediente
	 * @return Lista de productos que usan este ingrediente en su receta
	 */
	@Query("""
			SELECT pr FROM ProductoReceta pr
			WHERE pr.ingrediente.id = :ingredienteId
			ORDER BY pr.producto.nombre
			""")
	List<ProductoReceta> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);

	/**
	 * Verifica si existe una relación receta entre un producto e ingrediente.
	 * @param productoId ID del producto
	 * @param ingredienteId ID del ingrediente
	 * @return true si existe la relación
	 */
	@Query("""
			SELECT CASE WHEN COUNT(pr) > 0 THEN true ELSE false END
			FROM ProductoReceta pr
			WHERE pr.producto.id = :productoId AND pr.ingrediente.id = :ingredienteId
			""")
	boolean existsByProductoIdAndIngredienteId(
		@Param("productoId") Long productoId,
		@Param("ingredienteId") Long ingredienteId
	);

	/**
	 * Obtiene todas las líneas de receta con ingredientes que tienen stock bajo.
	 * @param umbralStock Umbral de stock (ej: 5)
	 * @return Líneas de receta con ingredientes bajo stock
	 */
	@Query("""
			SELECT pr FROM ProductoReceta pr
			WHERE pr.ingrediente.stockActual <= :umbralStock
			ORDER BY pr.ingrediente.stockActual ASC, pr.producto.nombre
			""")
	List<ProductoReceta> findRecetasConIngredienteBajoStock(@Param("umbralStock") int umbralStock);
}
