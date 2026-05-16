package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Asiento;
import com.diep.coffeeguin_backend.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    public List<Asiento> obtenerTodosLosAsientos() {
        return asientoRepository.findAll();
    }

    public Asiento crearAsiento(Asiento nuevoAsiento) {
        return asientoRepository.save(nuevoAsiento);
    }

    public Asiento obtenerAsientoPorId(Integer id) {
        return asientoRepository.findById(id).orElse(null);
    }

    public Asiento actualizarEstadoAsiento(Integer id, Boolean estaOcupado) {
        Asiento asientoExistente = asientoRepository.findById(id).orElse(null);
        if (asientoExistente != null) {
            asientoExistente.setOcupado(estaOcupado);
            return asientoRepository.save(asientoExistente);
        }
        return null;
    }
}