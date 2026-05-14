package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Mesa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class MesaDAOImpl implements MesaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Mesa> findAll() {
        return entityManager.createQuery("from Mesa", Mesa.class).getResultList();
    }

    @Override
    @Transactional
    public Mesa save(Mesa mesa) {
        if (mesa.getId() != null && mesa.getId() == 0) {
            mesa.setId(null);
        }
        if (mesa.getId() == null) {
            entityManager.persist(mesa);
            return mesa;
        }
        return entityManager.merge(mesa);
    }

    @Override
    @Transactional(readOnly = true)
    public Mesa findById(Integer id) {
        return entityManager.find(Mesa.class, id);
    }
}
