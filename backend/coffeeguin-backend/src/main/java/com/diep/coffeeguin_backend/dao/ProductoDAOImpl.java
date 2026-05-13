package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.db.DBConnection;
import com.diep.coffeeguin_backend.model.Alimento;
import com.diep.coffeeguin_backend.model.Bebida;
import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.Producto;
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
public class ProductoDAOImpl implements ProductoDAO {

	private final DBConnection dbConnection;
	private final CategoriaDAO categoriaDAO;

	public ProductoDAOImpl(DBConnection dbConnection, CategoriaDAO categoriaDAO) {
		this.dbConnection = dbConnection;
		this.categoriaDAO = categoriaDAO;
		crearTablasSiNoExisten();
	}

	@Override
	public void agregar(Producto p) {
		String sql = "INSERT INTO producto (nombre, precio, descripcion, tipo, stock_actual, umbral_alerta, categoria_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, p.getNombre());
			statement.setDouble(2, p.getPrecio());
			statement.setString(3, p.getDescripcion());
			statement.setString(4, resolverTipo(p));
			if (p instanceof Ingrediente ingrediente) {
				statement.setInt(5, ingrediente.getStockActual());
				statement.setInt(6, ingrediente.getUmbralAlerta());
			} else {
				statement.setNull(5, java.sql.Types.INTEGER);
				statement.setNull(6, java.sql.Types.INTEGER);
			}
			if (p.getCategoria() != null && p.getCategoria().getId() != null) {
				statement.setLong(7, p.getCategoria().getId());
			} else {
				statement.setNull(7, java.sql.Types.BIGINT);
			}
			statement.executeUpdate();

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					p.setId(generatedKeys.getLong(1));
				}
			}

			sincronizarIngredientes(connection, p);
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo registrar el producto", exception);
		}
	}

	@Override
	public void eliminar(Producto p) {
		if (p.getId() == null) {
			throw new IllegalArgumentException("El producto debe tener id para eliminarse");
		}

		String sql = "DELETE FROM producto WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, p.getId());
			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe un producto con id " + p.getId());
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo eliminar el producto", exception);
		}
	}

	@Override
	public Producto buscarPorId(int id) {
		return buscarPorIdInterno(id, true);
	}

	@Override
	public List<Producto> listarTodos() {
		String sql = "SELECT id, nombre, precio, descripcion, tipo, stock_actual, umbral_alerta, categoria_id FROM producto ORDER BY id";
		List<Producto> productos = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql);
			 ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Producto producto = mapearProducto(resultSet);
				if (!(producto instanceof Ingrediente)) {
					producto.setIngredientes(cargarIngredientes(connection, producto.getId()));
				}
				productos.add(producto);
			}
			return productos;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los productos", exception);
		}
	}

	@Override
	public List<Producto> listarPorCategoria(Categoria categoria) {
		if (categoria == null || categoria.getId() == null) {
			throw new IllegalArgumentException("La categoria debe tener id para consultar sus productos");
		}

		String sql = "SELECT id, nombre, precio, descripcion, tipo, stock_actual, umbral_alerta, categoria_id FROM producto WHERE categoria_id = ? ORDER BY id";
		List<Producto> productos = new ArrayList<>();
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, categoria.getId());
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Producto producto = mapearProducto(resultSet);
					if (!(producto instanceof Ingrediente)) {
						producto.setIngredientes(cargarIngredientes(connection, producto.getId()));
					}
					productos.add(producto);
				}
			}
			return productos;
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo listar los productos por categoria", exception);
		}
	}

	private Producto buscarPorIdInterno(int id, boolean cargarIngredientes) {
		String sql = "SELECT id, nombre, precio, descripcion, tipo, stock_actual, umbral_alerta, categoria_id FROM producto WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}

				Producto producto = mapearProducto(resultSet);
				if (cargarIngredientes && !(producto instanceof Ingrediente)) {
					producto.setIngredientes(cargarIngredientes(connection, producto.getId()));
				}
				return producto;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo buscar el producto", exception);
		}
	}

	@Override
	public void actualizar(Producto p) {
		if (p.getId() == null) {
			throw new IllegalArgumentException("El producto debe tener id para actualizarse");
		}

		String sql = "UPDATE producto SET nombre = ?, precio = ?, descripcion = ?, tipo = ?, stock_actual = ?, umbral_alerta = ?, categoria_id = ? WHERE id = ?";
		try (Connection connection = dbConnection.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, p.getNombre());
			statement.setDouble(2, p.getPrecio());
			statement.setString(3, p.getDescripcion());
			statement.setString(4, resolverTipo(p));
			if (p instanceof Ingrediente ingrediente) {
				statement.setInt(5, ingrediente.getStockActual());
				statement.setInt(6, ingrediente.getUmbralAlerta());
			} else {
				statement.setNull(5, java.sql.Types.INTEGER);
				statement.setNull(6, java.sql.Types.INTEGER);
			}
			if (p.getCategoria() != null && p.getCategoria().getId() != null) {
				statement.setLong(7, p.getCategoria().getId());
			} else {
				statement.setNull(7, java.sql.Types.BIGINT);
			}
			statement.setLong(8, p.getId());

			if (statement.executeUpdate() == 0) {
				throw new NoSuchElementException("No existe un producto con id " + p.getId());
			}

			sincronizarIngredientes(connection, p);
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo actualizar el producto", exception);
		}
	}

	private void crearTablasSiNoExisten() {
		String productoSql = "CREATE TABLE IF NOT EXISTS producto ("
			+ "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, "
			+ "nombre VARCHAR(150) NOT NULL, "
			+ "precio DOUBLE PRECISION NOT NULL, "
			+ "descripcion TEXT, "
			+ "tipo VARCHAR(50) NOT NULL, "
			+ "stock_actual INT, "
			+ "umbral_alerta INT, "
			+ "categoria_id BIGINT, "
			+ "CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)"
			+ ")";
		String migracionCategoriaSql = "ALTER TABLE producto ADD COLUMN IF NOT EXISTS categoria_id BIGINT";
		String migracionDescripcionSql = "ALTER TABLE producto ADD COLUMN IF NOT EXISTS descripcion TEXT";
		String recetaSql = "CREATE TABLE IF NOT EXISTS producto_receta ("
			+ "producto_id BIGINT NOT NULL, "
			+ "ingrediente_id BIGINT NOT NULL, "
			+ "PRIMARY KEY (producto_id, ingrediente_id), "
			+ "CONSTRAINT fk_producto_receta_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE, "
			+ "CONSTRAINT fk_producto_receta_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES producto(id) ON DELETE CASCADE"
			+ ")";
		try (Connection connection = dbConnection.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute(productoSql);
			statement.execute(migracionCategoriaSql);
			statement.execute(migracionDescripcionSql);
			statement.execute(recetaSql);
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo preparar la tabla de productos", exception);
		}
	}

	private Producto mapearProducto(ResultSet resultSet) throws SQLException {
		String tipo = resultSet.getString("tipo");
		Producto producto = switch (tipo) {
			case "ingrediente" -> {
				Ingrediente ingrediente = new Ingrediente();
				ingrediente.setStockActual(resultSet.getInt("stock_actual"));
				ingrediente.setUmbralAlerta(resultSet.getInt("umbral_alerta"));
				yield ingrediente;
			}
			case "alimento" -> new Alimento();
			case "bebida" -> new Bebida();
			default -> new Bebida();
		};
		producto.setId(resultSet.getLong("id"));
		producto.setNombre(resultSet.getString("nombre"));
		producto.setPrecio(resultSet.getDouble("precio"));
		producto.setDescripcion(resultSet.getString("descripcion"));
		producto.setTipo(tipo);
		Long categoriaId = resultSet.getObject("categoria_id", Long.class);
		if (categoriaId != null) {
			Categoria categoria = categoriaDAO.buscarPorId(categoriaId.intValue());
			producto.setCategoria(categoria);
		}
		return producto;
	}

	private List<Ingrediente> cargarIngredientes(Connection connection, Long productoId) throws SQLException {
		String sql = "SELECT ingrediente_id FROM producto_receta WHERE producto_id = ? ORDER BY ingrediente_id";
		List<Ingrediente> ingredientes = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, productoId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Producto ingrediente = buscarPorIdInterno(resultSet.getInt("ingrediente_id"), false);
					if (ingrediente instanceof Ingrediente ingredienteModelo) {
						ingredientes.add(ingredienteModelo);
					}
				}
			}
		}
		return ingredientes;
	}

	private void sincronizarIngredientes(Connection connection, Producto producto) throws SQLException {
		if (producto.getId() == null) {
			return;
		}

		try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM producto_receta WHERE producto_id = ?")) {
			deleteStatement.setLong(1, producto.getId());
			deleteStatement.executeUpdate();
		}

		if (producto.getIngredientes() == null || producto.getIngredientes().isEmpty()) {
			return;
		}

		String insertSql = "INSERT INTO producto_receta (producto_id, ingrediente_id) VALUES (?, ?)";
		try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
			for (Ingrediente ingrediente : producto.getIngredientes()) {
				if (ingrediente != null && ingrediente.getId() != null) {
					insertStatement.setLong(1, producto.getId());
					insertStatement.setLong(2, ingrediente.getId());
					insertStatement.addBatch();
				}
			}
			insertStatement.executeBatch();
		}
	}

	private String resolverTipo(Producto producto) {
		if (producto.getTipo() != null && !producto.getTipo().isBlank()) {
			return producto.getTipo();
		}
		if (producto instanceof Ingrediente) {
			return "ingrediente";
		}
		if (producto instanceof Alimento) {
			return "alimento";
		}
		return "bebida";
	}
}