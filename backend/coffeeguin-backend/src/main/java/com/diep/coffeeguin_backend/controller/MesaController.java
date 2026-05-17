package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public List<Mesa> obtenerTodasLasMesas() {
        return mesaService.obtenerTodasLasMesas();
    }
    
    @GetMapping("/{estado}")
    public List<Mesa> obtenerTodasLasMesasConEstado(@PathVariable String estado) {
        return mesaService.obtenerTodasLasMesasConEstado(estado);
    }

    @PostMapping
    public Map<String, Object> crearMesa(@RequestBody Map<String, Object> payload) {
        Integer numero = (Integer) payload.get("numero");
        String estado = (String) payload.get("estado");
        Integer cantidadAsientos = (Integer) payload.get("cantidadAsientos");
        
        Mesa mesaCreada = mesaService.crearMesa(numero, estado, cantidadAsientos);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", mesaCreada.getId());
        response.put("numero", mesaCreada.getNumero());
        response.put("estado", mesaCreada.getEstado());
        response.put("cantidadAsientos", mesaCreada.getAsientos().size());
        
        return response;
    }

    @PutMapping("/{id}/estado")
    public Mesa actualizarEstadoMesa(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        return mesaService.actualizarEstadoMesa(id, nuevoEstado);
    }

    @PutMapping("/{id}/asientos")
    public Map<String, Object> actualizarCantidadAsientos(@PathVariable Integer id, @RequestParam Integer cantidad) {
        Mesa mesaActualizada = mesaService.actualizarCantidadAsientos(id, cantidad);
        
        Map<String, Object> response = new LinkedHashMap<>();
        if (mesaActualizada != null) {
            response.put("id", mesaActualizada.getId());
            response.put("numero", mesaActualizada.getNumero());
            response.put("mensaje", "Asientos actualizados correctamente");
            response.put("nuevaCantidadAsientos", mesaActualizada.getAsientos().size());
        } else {
            response.put("error", "Mesa no encontrada");
        }
        return response;
    }
}