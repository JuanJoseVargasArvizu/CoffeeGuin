package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Venta;
import java.util.List;

public interface VentaDAO {
    // Buscar todas las ventas 
    List<Venta> findAll();
    
    // Guardar una nueva venta 
    Venta save(Venta venta);
    
    // Buscar una venta en específico por su ID
    Venta findById(Integer id);
}
