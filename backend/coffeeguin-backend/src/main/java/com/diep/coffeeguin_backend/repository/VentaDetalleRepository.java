package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.VentaDetalle;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Integer> {
	@Query("""
			SELECT p.id as productoId,
			       p.nombre as productoNombre,
			       COALESCE(c.nombre, 'Sin categoria') as categoriaNombre,
			       SUM(vd.cantidad) as cantidadTotal,
			       SUM(vd.subtotalLinea) as totalVendido
			FROM VentaDetalle vd
			JOIN vd.venta v
			JOIN vd.producto p
			LEFT JOIN p.categoria c
			WHERE v.fecha BETWEEN :inicio AND :fin
			GROUP BY p.id, p.nombre, c.nombre
			ORDER BY totalVendido DESC
			""")
	List<VentasPorProductoResumen> resumirVentasPorProducto(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query("""
			SELECT c.id as categoriaId,
			       COALESCE(c.nombre, 'Sin categoria') as categoriaNombre,
			       SUM(vd.cantidad) as cantidadTotal,
			       SUM(vd.subtotalLinea) as totalVendido
			FROM VentaDetalle vd
			JOIN vd.venta v
			JOIN vd.producto p
			LEFT JOIN p.categoria c
			WHERE v.fecha BETWEEN :inicio AND :fin
			GROUP BY c.id, c.nombre
			ORDER BY totalVendido DESC
			""")
	List<VentasPorCategoriaResumen> resumirVentasPorCategoria(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}