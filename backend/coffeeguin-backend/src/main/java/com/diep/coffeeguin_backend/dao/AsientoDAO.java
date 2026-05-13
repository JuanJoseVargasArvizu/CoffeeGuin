package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Asiento;
import java.util.List;

public interface AsientoDAO {
    List<Asiento> findAll();
    Asiento save(Asiento asiento);
    Asiento findById(Integer id);
}
