package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Venta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository 
public class VentaDAOImpl implements VentaDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return entityManager.createQuery("from Venta", Venta.class).getResultList();
    }

    @Override
    @Transactional
    public Venta save(Venta venta) {
        if (venta.getIdVenta() == null || venta.getIdVenta() == 0) {
            entityManager.persist(venta); 
            return venta;
        } else {
            return entityManager.merge(venta); 
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Integer id) {
        return entityManager.find(Venta.class, id);
    }
}