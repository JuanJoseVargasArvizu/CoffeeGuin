package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.ProductoMesa;
import com.diep.coffeeguin_backend.model.ProductoMesaId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoMesaRepository extends JpaRepository<ProductoMesa, ProductoMesaId> {
	List<ProductoMesa> findByMesaIdAndEstadoPago(Long mesaId, String estadoPago);
	List<ProductoMesa> findByMesa_IdAndEstadoPago(Integer mesaId, String estadoPago);

	@Query("SELECT pm FROM ProductoMesa pm JOIN FETCH pm.producto p WHERE pm.id.mesaId = :mesaId AND pm.estadoPago = :estadoPago")
	List<ProductoMesa> findPendientesConProductoByMesaIdAndEstadoPago(@Param("mesaId") Long mesaId, @Param("estadoPago") String estadoPago);

	Optional<ProductoMesa> findByMesa_IdAndProducto_IdAndEstadoPago(Integer mesaId, Long productoId, String estadoPago);

	@Modifying
	@Query("UPDATE ProductoMesa pm SET pm.estadoPago = :nuevoEstado WHERE pm.mesa.id = :mesaId AND pm.estadoPago = :estadoActual")
	int actualizarEstadoPagoPorMesa(@Param("mesaId") Integer mesaId, @Param("estadoActual") String estadoActual, @Param("nuevoEstado") String nuevoEstado);
}