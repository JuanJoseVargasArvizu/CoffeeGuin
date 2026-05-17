package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.repository.projection.ReporteFinancieroProjection;
import com.diep.coffeeguin_backend.repository.projection.VentaResumenProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
	@Query(value = """
			SELECT v.id_venta AS \"idVenta\",
			       v.fecha AS \"fecha\",
			       v.subtotal AS \"subtotal\",
			       v.total_final AS \"totalFinal\",
			       COALESCE(CAST(m.numero AS VARCHAR), 'Sin mesa') AS \"numeroMesa\",
			       COALESCE(c.nombre, 'Público general') AS \"nombreCliente\"
			FROM venta v
			LEFT JOIN cliente c ON c.id = v.cliente_id
			LEFT JOIN mesa m ON m.id = v.mesa_id
			WHERE v.fecha BETWEEN :inicio AND :fin
			ORDER BY v.fecha DESC, v.id_venta DESC
			""", nativeQuery = true)
	List<VentaResumenProjection> buscarResumenVentas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	@Query(value = """
			SELECT COUNT(*) AS \"totalTransacciones\",
			       COALESCE(SUM(v.total_final), 0) AS \"balanceNeto\"
			FROM venta v
			WHERE v.fecha BETWEEN :inicio AND :fin
			""", nativeQuery = true)
	ReporteFinancieroProjection resumirBalancePorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

	List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

	List<Venta> findByFechaGreaterThanEqual(LocalDateTime inicio);

	List<Venta> findByFechaLessThanEqual(LocalDateTime fin);
}