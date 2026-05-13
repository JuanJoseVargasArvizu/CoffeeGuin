package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Asiento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class AsientoDAOImpl implements AsientoDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Asiento> findAll() {
        return entityManager.createQuery("from Asiento", Asiento.class).getResultList();
    }

    @Override
    @Transactional
    public Asiento save(Asiento asiento) {
        // REVISA ESTA LÍNEA: Cambia getIdAsiento() por getId() o getNumero() si es necesario
        if (asiento.getId() == null || asiento.getId() == 0) {
            entityManager.persist(asiento);
            return asiento;
        } else {
            return entityManager.merge(asiento);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Asiento findById(Integer id) {
        return entityManager.find(Asiento.class, id);
    }
}