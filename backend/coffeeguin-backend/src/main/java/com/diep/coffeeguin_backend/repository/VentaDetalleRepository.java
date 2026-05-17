package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.VentaDetalle;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import com.diep.coffeeguin_backend.repository.projection.AnaliticaGraficaProjection;
import com.diep.coffeeguin_backend.repository.projection.ProductosVendidosPorMesaProjection;
import com.diep.coffeeguin_backend.repository.projection.VentaDetalleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Integer> {
	@Query(value = """
			SELECT p.nombre AS \"nombreProducto\",
			       vd.cantidad AS \"cantidad\",
			       vd.precio_unitario AS \"precioUnitario\"
			FROM venta_detalle vd
			JOIN producto p ON p.id = vd.producto_id
			WHERE vd.venta_id = :ventaId
			ORDER BY vd.id ASC
			""", nativeQuery = true)
	List<VentaDetalleProjection> buscarDetalleDeVenta(@Param("ventaId") Integer ventaId);

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

	@Query(value = """
			SELECT p.nombre AS \"etiqueta\",
			       SUM(vd.cantidad) AS \"cantidadVendida\",
			       COALESCE(SUM(vd.subtotal_linea), 0) AS \"ingresos\"
			FROM venta_detalle vd
			JOIN venta v ON v.id_venta = vd.venta_id
			JOIN producto p ON p.id = vd.producto_id
			WHERE v.fecha BETWEEN :inicio AND :fin
			GROUP BY p.id, p.nombre
			ORDER BY SUM(vd.cantidad) DESC, SUM(vd.subtotal_linea) DESC, p.nombre ASC
			""", nativeQuery = true)
	List<AnaliticaGraficaProjection> analiticaPorProducto(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query(value = """
			SELECT COALESCE(c.nombre, 'Sin categoria') AS \"etiqueta\",
			       SUM(vd.cantidad) AS \"cantidadVendida\",
			       COALESCE(SUM(vd.subtotal_linea), 0) AS \"ingresos\"
			FROM venta_detalle vd
			JOIN venta v ON v.id_venta = vd.venta_id
			JOIN producto p ON p.id = vd.producto_id
			LEFT JOIN categoria c ON c.id = p.categoria_id
			WHERE v.fecha BETWEEN :inicio AND :fin
			GROUP BY c.id, c.nombre
			ORDER BY SUM(vd.cantidad) DESC, SUM(vd.subtotal_linea) DESC, COALESCE(c.nombre, 'Sin categoria') ASC
			""", nativeQuery = true)
	List<AnaliticaGraficaProjection> analiticaPorCategoria(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query(value = """
			SELECT COALESCE(CAST(m.numero AS VARCHAR), 'Sin mesa') AS \"numeroMesa\",
			       p.nombre AS \"nombreProducto\",
			       SUM(vd.cantidad) AS \"cantidadVendida\",
			       COALESCE(SUM(vd.subtotal_linea), 0) AS \"ingresos\"
			FROM venta_detalle vd
			JOIN venta v ON v.id_venta = vd.venta_id
			LEFT JOIN mesa m ON m.id = v.mesa_id
			JOIN producto p ON p.id = vd.producto_id
			WHERE v.fecha BETWEEN :inicio AND :fin
			GROUP BY m.numero, p.nombre
			ORDER BY COALESCE(m.numero, 0) ASC, SUM(vd.cantidad) DESC, p.nombre ASC
			""", nativeQuery = true)
	List<ProductosVendidosPorMesaProjection> productosVendidosPorMesa(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}