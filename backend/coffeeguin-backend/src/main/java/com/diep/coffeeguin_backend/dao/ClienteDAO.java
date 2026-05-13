package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Cliente;

import java.util.List;

public interface ClienteDAO {

	void agregar(Cliente cliente);

	void eliminar(Cliente cliente);

	Cliente buscarPorId(int id);

	List<Cliente> listarTodos();

	List<Cliente> listarActivos();

	void actualizar(Cliente cliente);

	Cliente buscarPorEmail(String email);

	Cliente buscarPorTelefono(String telefono);

	List<Cliente> listarPorEstrategiaDescuento(int estrategiaId);
}
