package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dao.MesaDAO;
import com.diep.coffeeguin_backend.model.Mesa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaDAO mesaDAO;

    // obtiene todas las mesas 
    @GetMapping
    public List<Mesa> obtenerTodasLasMesas() {
        return mesaDAO.findAll();
    }

    // da alta a una nueva mesa en el sistema 
    @PostMapping
    public Mesa crearMesa(@RequestBody Mesa nuevaMesa) {
        if (nuevaMesa.getEstado() == null || nuevaMesa.getEstado().isEmpty()) {
            nuevaMesa.setEstado("Libre");
        }
        return mesaDAO.save(nuevaMesa);
    }

    // cambia el estado de una mesa 
    @PutMapping("/{id}/estado")
    public Mesa actualizarEstadoMesa(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        Mesa mesaExistente = mesaDAO.findById(id);
        
        if (mesaExistente != null) {
            mesaExistente.setEstado(nuevoEstado);
            return mesaDAO.save(mesaExistente);
        }
        return null; 
    }
}

