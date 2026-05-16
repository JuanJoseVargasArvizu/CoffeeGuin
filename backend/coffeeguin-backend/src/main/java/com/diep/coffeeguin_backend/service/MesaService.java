package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.findAll();
    }

    public Mesa crearMesa(Mesa nuevaMesa) {
        if (nuevaMesa.getEstado() == null || nuevaMesa.getEstado().isEmpty()) {
            nuevaMesa.setEstado("Libre");
        }
        return mesaRepository.save(nuevaMesa);
    }

    public Mesa actualizarEstadoMesa(Integer id, String nuevoEstado) {
        Mesa mesaExistente = mesaRepository.findById(id).orElse(null);
        if (mesaExistente != null) {
            mesaExistente.setEstado(nuevoEstado);
            return mesaRepository.save(mesaExistente);
        }
        return null;
    }
}
