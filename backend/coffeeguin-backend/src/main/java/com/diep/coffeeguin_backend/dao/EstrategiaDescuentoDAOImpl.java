package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.db.DBConnection;
import com.diep.coffeeguin_backend.model.DescuentoFijo;
import com.diep.coffeeguin_backend.model.DescuentoPorcentaje;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class EstrategiaDescuentoDAOImpl implements EstrategiaDescuentoDAO {

	private final DBConnection dbConnection;

	public EstrategiaDescuentoDAOImpl(DBConnection dbConnection) {
		this.dbConnection = dbConnection;
		crearTablaSiNoExiste();
	}

	@Override
	public void agregar(EstrategiaDescuento estrategia) {
		String sql = "INSERT INTO estrategia_descuento (nombre, descripcion, tipo_estrategia, porcentaje, monto_fijo, activa) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, estrategia.getNombre());
			statement.setString(2, estrategia.getDescripcion());
			
			if (estrategia instanceof DescuentoPorcentaje) {
				DescuentoPorcentaje desc = (DescuentoPorcentaje) estrategia;
				statement.setString(3, "PORCENTAJE");
				statement.setDouble(4, desc.getPorcentaje());
				statement.setDouble(5, 0.0);
			} else if (estrategia instanceof DescuentoFijo) {
				DescuentoFijo desc = (DescuentoFijo) estrategia;
				statement.setString(3, "FIJO");
				statement.setDouble(4, 0.0);
				statement.setDouble(5, desc.getMontoFijo());
			} else {
				throw new IllegalArgumentException("Tipo de estrategia no soportado");
			}
			
			statement.setBoolean(6, estrategia.getActiva());
			try (ResultSet generatedKeys = statement.executeQuery()) {
				if (generatedKeys.next()) {
					estrategia.setId(generatedKeys.getInt("id"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo registrar la estrategia de descuento", exception);
		}
	}

	@Override
	public void eliminar(EstrategiaDescuento estrategia) {
		if (estrategia.getId() == null) {
			throw new IllegalArgumentException("La estrategia debe tener id para eliminarse");
		}
		
		String sql = "DELETE FROM estrategia_descuento WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, estrategia.getId());
			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe una estrategia con id " + estrategia.getId());
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo eliminar la estrategia de descuento", exception);
		}
	}

	@Override
	public EstrategiaDescuento buscarPorId(int id) {
		String sql = "SELECT id, nombre, descripcion, tipo_estrategia, porcentaje, monto_fijo, activa FROM estrategia_descuento WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}
				return mapearEstrategia(resultSet);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo buscar la estrategia de descuento", exception);
		}
	}

	@Override
	public List<EstrategiaDescuento> listarTodas() {
		String sql = "SELECT id, nombre, descripcion, tipo_estrategia, porcentaje, monto_fijo, activa FROM estrategia_descuento ORDER BY id";
		List<EstrategiaDescuento> estrategias = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				estrategias.add(mapearEstrategia(resultSet));
			}
			return estrategias;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar las estrategias de descuento", exception);
		}
	}

	@Override
	public List<EstrategiaDescuento> listarActivas() {
		String sql = "SELECT id, nombre, descripcion, tipo_estrategia, porcentaje, monto_fijo, activa FROM estrategia_descuento WHERE activa = true ORDER BY id";
		List<EstrategiaDescuento> estrategias = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				estrategias.add(mapearEstrategia(resultSet));
			}
			return estrategias;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar las estrategias activas", exception);
		}
	}

	@Override
	public void actualizar(EstrategiaDescuento estrategia) {
		if (estrategia.getId() == null) {
			throw new IllegalArgumentException("La estrategia debe tener id para actualizarse");
		}
		
		String sql = "UPDATE estrategia_descuento SET nombre = ?, descripcion = ?, porcentaje = ?, monto_fijo = ?, activa = ? WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, estrategia.getNombre());
			statement.setString(2, estrategia.getDescripcion());
			
			if (estrategia instanceof DescuentoPorcentaje) {
				DescuentoPorcentaje desc = (DescuentoPorcentaje) estrategia;
				statement.setDouble(3, desc.getPorcentaje());
				statement.setDouble(4, 0.0);
			} else if (estrategia instanceof DescuentoFijo) {
				DescuentoFijo desc = (DescuentoFijo) estrategia;
				statement.setDouble(3, 0.0);
				statement.setDouble(4, desc.getMontoFijo());
			} else {
				throw new IllegalArgumentException("Tipo de estrategia no soportado");
			}
			
			statement.setBoolean(5, estrategia.getActiva());
			statement.setInt(6, estrategia.getId());
			
			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe una estrategia con id " + estrategia.getId());
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo actualizar la estrategia de descuento", exception);
		}
	}

	@Override
	public List<DescuentoPorcentaje> listarPorcentajes() {
		String sql = "SELECT id, nombre, descripcion, porcentaje, activa FROM estrategia_descuento WHERE tipo_estrategia = 'PORCENTAJE' ORDER BY id";
		List<DescuentoPorcentaje> estrategias = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				DescuentoPorcentaje desc = new DescuentoPorcentaje();
				desc.setId(resultSet.getInt("id"));
				desc.setNombre(resultSet.getString("nombre"));
				desc.setDescripcion(resultSet.getString("descripcion"));
				desc.setPorcentaje(resultSet.getDouble("porcentaje"));
				desc.setActiva(resultSet.getBoolean("activa"));
				estrategias.add(desc);
			}
			return estrategias;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los descuentos por porcentaje", exception);
		}
	}

	@Override
	public List<DescuentoFijo> listarFijos() {
		String sql = "SELECT id, nombre, descripcion, monto_fijo, activa FROM estrategia_descuento WHERE tipo_estrategia = 'FIJO' ORDER BY id";
		List<DescuentoFijo> estrategias = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				DescuentoFijo desc = new DescuentoFijo();
				desc.setId(resultSet.getInt("id"));
				desc.setNombre(resultSet.getString("nombre"));
				desc.setDescripcion(resultSet.getString("descripcion"));
				desc.setMontoFijo(resultSet.getDouble("monto_fijo"));
				desc.setActiva(resultSet.getBoolean("activa"));
				estrategias.add(desc);
			}
			return estrategias;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los descuentos fijos", exception);
		}
	}

	private EstrategiaDescuento mapearEstrategia(ResultSet resultSet) throws SQLException {
		String tipo = resultSet.getString("tipo_estrategia");
		int id = resultSet.getInt("id");
		String nombre = resultSet.getString("nombre");
		String descripcion = resultSet.getString("descripcion");
		boolean activa = resultSet.getBoolean("activa");
		
		if ("PORCENTAJE".equals(tipo)) {
			DescuentoPorcentaje desc = new DescuentoPorcentaje();
			desc.setId(id);
			desc.setNombre(nombre);
			desc.setDescripcion(descripcion);
			desc.setPorcentaje(resultSet.getDouble("porcentaje"));
			desc.setActiva(activa);
			return desc;
		} else if ("FIJO".equals(tipo)) {
			DescuentoFijo desc = new DescuentoFijo();
			desc.setId(id);
			desc.setNombre(nombre);
			desc.setDescripcion(descripcion);
			desc.setMontoFijo(resultSet.getDouble("monto_fijo"));
			desc.setActiva(activa);
			return desc;
		}
		
		return null;
	}

	private void crearTablaSiNoExiste() {
		String sql = "CREATE TABLE IF NOT EXISTS estrategia_descuento (" +
				"id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
				"nombre VARCHAR(100) NOT NULL, " +
				"descripcion TEXT, " +
				"tipo_estrategia VARCHAR(20) NOT NULL, " +
				"porcentaje DOUBLE PRECISION, " +
				"monto_fijo DOUBLE PRECISION, " +
				"activa BOOLEAN DEFAULT true" +
				")";
		try (Connection connection = dbConnection.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(sql);
			statement.execute("ALTER TABLE estrategia_descuento ALTER COLUMN porcentaje DROP NOT NULL");
			statement.execute("ALTER TABLE estrategia_descuento ALTER COLUMN monto_fijo DROP NOT NULL");
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo preparar la tabla de estrategias_descuento", exception);
		}
	}
}
