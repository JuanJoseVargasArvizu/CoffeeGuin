package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Reporte;
import java.util.List;

public interface ReporteDAO {
    List<Reporte> findAll();
    Reporte save(Reporte reporte);
    Reporte findById(Integer id);
}