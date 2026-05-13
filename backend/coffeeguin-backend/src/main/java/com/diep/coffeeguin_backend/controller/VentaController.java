package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dao.VentaDAO;
import com.diep.coffeeguin_backend.dao.MesaDAO;
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

    @GetMapping
    public List<Venta> obtenerHistorial() {
        return ventaDAO.findAll();
    }

    @GetMapping("/{id}")
    public Venta obtenerDetalleVenta(@PathVariable Integer id) {
        return ventaDAO.findById(id);
    }

    @PostMapping
    public Venta registrarVenta(@RequestBody Venta nuevaVenta) {
        
        nuevaVenta.setFecha(LocalDateTime.now());
        double subtotalCalculado = 0.0;
        //calculo automatico
        if (nuevaVenta.getProductos() != null && !nuevaVenta.getProductos().isEmpty()) {
            for (Producto p : nuevaVenta.getProductos()) {
                subtotalCalculado += p.getPrecio(); 
            }
        }
        nuevaVenta.setSubtotal(subtotalCalculado);

        // Lógica de descuento
        double totalFinal = subtotalCalculado;
        if (nuevaVenta.getCliente() != null) {
            totalFinal = subtotalCalculado * 0.90; 
        }
        nuevaVenta.setTotalFinal(totalFinal);

        // asignacion de mesa
        if (nuevaVenta.getMesa() != null) {
            Mesa mesaAsignada = mesaDAO.findById(nuevaVenta.getMesa().getId());
            if (mesaAsignada != null) {
                mesaAsignada.setEstado("Ocupada");
                mesaDAO.save(mesaAsignada); 
            }
        }

        //codigo para restar ingredientes 

        
        Venta ventaGuardada = ventaDAO.save(nuevaVenta);

        // Genera recibo detallado
        System.out.println("--- RECIBO GENERADO ---");
        System.out.println("Fecha: " + ventaGuardada.getFecha());
        System.out.println("Subtotal: $" + ventaGuardada.getSubtotal());
        System.out.println("Total a Pagar: $" + ventaGuardada.getTotalFinal());
        System.out.println("-----------------------");

        return ventaGuardada;
    }
}