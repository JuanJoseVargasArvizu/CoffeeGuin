package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Mesa;
import java.util.List;

public interface MesaDAO {
    List<Mesa> findAll();
    Mesa save(Mesa mesa);
    Mesa findById(Integer id);
}
