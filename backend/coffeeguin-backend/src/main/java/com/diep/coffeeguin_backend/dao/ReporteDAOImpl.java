package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Reporte;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class ReporteDAOImpl implements ReporteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> findAll() {
        return entityManager.createQuery("from Reporte", Reporte.class).getResultList();
    }

    @Override
    @Transactional
    public Reporte save(Reporte reporte) {
        if (reporte.getIdReporte() == null || reporte.getIdReporte() == 0) {
            entityManager.persist(reporte);
            return reporte;
        } else {
            return entityManager.merge(reporte);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Reporte findById(Integer id) {
        return entityManager.find(Reporte.class, id);
    }
}