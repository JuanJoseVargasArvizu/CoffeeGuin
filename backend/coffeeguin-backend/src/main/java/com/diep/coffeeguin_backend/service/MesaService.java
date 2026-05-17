package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Asiento;
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

    public List<Mesa> obtenerTodasLasMesasConEstado(String estado) {
        return mesaRepository.findByEstado(estado);
    }

    public Mesa crearMesa(Integer numeroMesa, String estado, Integer cantidadAsientos) {
        Mesa nuevaMesa = new Mesa();
        nuevaMesa.setNumero(numeroMesa);
        
        if (estado == null || estado.isEmpty()) {
            nuevaMesa.setEstado("Libre");
        } else {
            nuevaMesa.setEstado(estado);
        }

        if (cantidadAsientos != null && cantidadAsientos > 0) {
            for (int i = 1; i <= cantidadAsientos; i++) {
                Asiento nuevoAsiento = new Asiento();
                nuevoAsiento.setNumero(i);
                nuevoAsiento.setOcupado(false);
                nuevaMesa.getAsientos().add(nuevoAsiento);
            }
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

    public Mesa actualizarCantidadAsientos(Integer idMesa, Integer nuevaCantidad) {
        Mesa mesa = mesaRepository.findById(idMesa).orElse(null);
        
        if (mesa != null && nuevaCantidad != null && nuevaCantidad >= 0) {
            int cantidadActual = mesa.getAsientos().size();

            if (nuevaCantidad > cantidadActual) {
                for (int i = cantidadActual + 1; i <= nuevaCantidad; i++) {
                    Asiento nuevoAsiento = new Asiento();
                    nuevoAsiento.setNumero(i);
                    nuevoAsiento.setOcupado(false);
                    mesa.getAsientos().add(nuevoAsiento);
                }
            } else if (nuevaCantidad < cantidadActual) {
                for (int i = cantidadActual - 1; i >= nuevaCantidad; i--) {
                    mesa.getAsientos().remove(i);
                }
            }
            
            return mesaRepository.save(mesa);
        }
        return null;
    }
}
