package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.db.DBConnection;
import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import com.diep.coffeeguin_backend.dao.EstrategiaDescuentoDAO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class ClienteDAOImpl implements ClienteDAO {

	private final DBConnection dbConnection;
	private final EstrategiaDescuentoDAO estrategiaDAO;

	public ClienteDAOImpl(DBConnection dbConnection, EstrategiaDescuentoDAO estrategiaDAO) {
		this.dbConnection = dbConnection;
		this.estrategiaDAO = estrategiaDAO;
		crearTablaSiNoExiste();
	}

	@Override
	public void agregar(Cliente cliente) {
		String sql = "INSERT INTO cliente (nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, new String[] { "id" })) {
			
			statement.setString(1, cliente.getNombre());
			statement.setString(2, cliente.getEmail());
			statement.setString(3, cliente.getTelefono());
			statement.setString(4, cliente.getDireccion());
			statement.setString(5, cliente.getPreferencias());
			statement.setString(6, cliente.getAlergias());
			statement.setString(7, cliente.getBebidaFavorita());
			statement.setString(8, cliente.getPlatoFavorito());
			
			if (cliente.getEstrategia() != null && cliente.getEstrategia().getId() != null) {
				statement.setLong(9, cliente.getEstrategia().getId());
			} else {
				statement.setNull(9, java.sql.Types.BIGINT);
			}
			
			LocalDateTime ahora = LocalDateTime.now();
			statement.setTimestamp(10, Timestamp.valueOf(ahora));
			statement.setTimestamp(11, Timestamp.valueOf(ahora));
			statement.setBoolean(12, cliente.getActivo());
			
			statement.executeUpdate();
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					cliente.setId(generatedKeys.getInt("id"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo registrar el cliente", exception);
		}
	}

	@Override
	public void eliminar(Cliente cliente) {
		if (cliente.getId() == null) {
			throw new IllegalArgumentException("El cliente debe tener id para eliminarse");
		}
		
		String sql = "DELETE FROM cliente WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, cliente.getId());
			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe un cliente con id " + cliente.getId());
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo eliminar el cliente", exception);
		}
	}

	@Override
	public Cliente buscarPorId(int id) {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}
				return mapearCliente(resultSet);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo buscar el cliente", exception);
		}
	}

	@Override
	public List<Cliente> listarTodos() {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente ORDER BY id";
		List<Cliente> clientes = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				clientes.add(mapearCliente(resultSet));
			}
			return clientes;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los clientes", exception);
		}
	}

	@Override
	public List<Cliente> listarActivos() {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente WHERE activo = true ORDER BY id";
		List<Cliente> clientes = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				clientes.add(mapearCliente(resultSet));
			}
			return clientes;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los clientes activos", exception);
		}
	}

	@Override
	public void actualizar(Cliente cliente) {
		if (cliente.getId() == null) {
			throw new IllegalArgumentException("El cliente debe tener id para actualizarse");
		}
		
		String sql = "UPDATE cliente SET nombre = ?, email = ?, telefono = ?, direccion = ?, preferencias = ?, alergias = ?, bebida_favorita = ?, plato_favorito = ?, estrategia_descuento_id = ?, fecha_actualizacion = ?, activo = ? WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, cliente.getNombre());
			statement.setString(2, cliente.getEmail());
			statement.setString(3, cliente.getTelefono());
			statement.setString(4, cliente.getDireccion());
			statement.setString(5, cliente.getPreferencias());
			statement.setString(6, cliente.getAlergias());
			statement.setString(7, cliente.getBebidaFavorita());
			statement.setString(8, cliente.getPlatoFavorito());
			
			if (cliente.getEstrategia() != null && cliente.getEstrategia().getId() != null) {
				statement.setLong(9, cliente.getEstrategia().getId());
			} else {
				statement.setNull(9, java.sql.Types.BIGINT);
			}
			
			statement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
			statement.setBoolean(11, cliente.getActivo());
			statement.setInt(12, cliente.getId());
			
			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe un cliente con id " + cliente.getId());
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo actualizar el cliente", exception);
		}
	}

	@Override
	public Cliente buscarPorEmail(String email) {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente WHERE email = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, email);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}
				return mapearCliente(resultSet);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo buscar el cliente por email", exception);
		}
	}

	@Override
	public Cliente buscarPorTelefono(String telefono) {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente WHERE telefono = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, telefono);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}
				return mapearCliente(resultSet);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo buscar el cliente por teléfono", exception);
		}
	}

	@Override
	public List<Cliente> listarPorEstrategiaDescuento(int estrategiaId) {
		String sql = "SELECT id, nombre, email, telefono, direccion, preferencias, alergias, bebida_favorita, plato_favorito, estrategia_descuento_id, fecha_registro, fecha_actualizacion, activo FROM cliente WHERE estrategia_descuento_id = ? ORDER BY id";
		List<Cliente> clientes = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, estrategiaId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					clientes.add(mapearCliente(resultSet));
				}
			}
			return clientes;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los clientes por estrategia de descuento", exception);
		}
	}

	private Cliente mapearCliente(ResultSet resultSet) throws SQLException {
		Cliente cliente = new Cliente();
		cliente.setId(resultSet.getInt("id"));
		cliente.setNombre(resultSet.getString("nombre"));
		cliente.setEmail(resultSet.getString("email"));
		cliente.setTelefono(resultSet.getString("telefono"));
		cliente.setDireccion(resultSet.getString("direccion"));
		cliente.setPreferencias(resultSet.getString("preferencias"));
		cliente.setAlergias(resultSet.getString("alergias"));
		cliente.setBebidaFavorita(resultSet.getString("bebida_favorita"));
		cliente.setPlatoFavorito(resultSet.getString("plato_favorito"));
		cliente.setFechaRegistro(resultSet.getTimestamp("fecha_registro") != null 
			? resultSet.getTimestamp("fecha_registro").toLocalDateTime() 
			: null);
		cliente.setFechaActualizacion(resultSet.getTimestamp("fecha_actualizacion") != null 
			? resultSet.getTimestamp("fecha_actualizacion").toLocalDateTime() 
			: null);
		cliente.setActivo(resultSet.getBoolean("activo"));

		// Cargar estrategia de descuento si existe (soportar int4 y bigint)
		Object estrategiaObj = resultSet.getObject("estrategia_descuento_id");
		if (estrategiaObj != null) {
			long estrategiaId = ((Number) estrategiaObj).longValue();
			EstrategiaDescuento estrategia = estrategiaDAO.buscarPorId((int) estrategiaId);
			if (estrategia != null) {
				cliente.setEstrategiaDescuento(estrategia);
			}
		}

		return cliente;
	}

	private void crearTablaSiNoExiste() {
		String sql = "CREATE TABLE IF NOT EXISTS cliente (" +
				"id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
				"nombre VARCHAR(100) NOT NULL, " +
				"email VARCHAR(100), " +
				"telefono VARCHAR(15), " +
				"direccion VARCHAR(255), " +
				"preferencias TEXT, " +
				"alergias VARCHAR(255), " +
				"bebida_favorita VARCHAR(255), " +
				"plato_favorito VARCHAR(255), " +
				"estrategia_descuento_id BIGINT, " +
				"fecha_registro TIMESTAMP, " +
				"fecha_actualizacion TIMESTAMP, " +
				"activo BOOLEAN DEFAULT true" +
				")";
		try (Connection connection = dbConnection.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(sql);
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo preparar la tabla de cliente", exception);
		}
	}
}
