package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dao.VentaDAO;
import com.diep.coffeeguin_backend.dao.MesaDAO;
import com.diep.coffeeguin_backend.dao.ProductoDAO;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaDAO ventaDAO;

    @Autowired
    private MesaDAO mesaDAO;

    @Autowired
    private ProductoDAO productoDAO;

    @GetMapping
    public List<Venta> obtenerHistorial() {
        return ventaDAO.findAll();
    }

    @PostMapping
    public Venta registrarVenta(@RequestBody Venta nuevaVenta) {
        
        // Asegurar la fecha
        if (nuevaVenta.getFecha() == null) {
            nuevaVenta.setFecha(LocalDateTime.now());
        }
        
        // Cálculo de subtotal
        double subtotalCalculado = 0.0;
        if (nuevaVenta.getProductos() != null) {
            for (Producto p : nuevaVenta.getProductos()) {
                subtotalCalculado += p.getPrecio(); 
            }
        }
        nuevaVenta.setSubtotal(subtotalCalculado);

        // Aplicar descuento
        if (nuevaVenta.getCliente() != null) {
            nuevaVenta.finalizarVenta(); 
            }

        // Ocupar Mesa
        if (nuevaVenta.getMesa() != null) {
            Mesa mesaAsignada = mesaDAO.findById(nuevaVenta.getMesa().getId());
            if (mesaAsignada != null) {
                mesaAsignada.setEstado("Ocupada");
                mesaDAO.save(mesaAsignada); 
            }
        }
        if (nuevaVenta.getProductos() != null) {
            for (Producto p : nuevaVenta.getProductos()) {
                Producto productoEnBD = productoDAO.buscarPorId(p.getId().intValue());
                
                if (productoEnBD != null) {
                    productoDAO.actualizar(productoEnBD);
                }
            }
        }
        Venta ventaGuardada = ventaDAO.save(nuevaVenta);
        return ventaGuardada; 
    }
}