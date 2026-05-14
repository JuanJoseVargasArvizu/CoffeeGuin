package com.diep.coffeeguin_backend.repository;

import com.diep.coffeeguin_backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
    List<Cliente> findByActivoTrueOrderById();
    List<Cliente> findByEstrategia_Id(Integer estrategiaId);
}
