package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.db.DBConnection;
import com.diep.coffeeguin_backend.model.Venta;
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
public class VentaDAOImpl implements VentaDAO {

    private final DBConnection dbConnection;

    public VentaDAOImpl(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
        crearTablaSiNoExiste();
    }

    @Override
    public List<Venta> findAll() {
        String sql = "SELECT id_venta, fecha, cliente_id, mesa_id, subtotal, total_final FROM venta ORDER BY id_venta";
        List<Venta> ventas = new ArrayList<>();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ventas.add(mapearVenta(resultSet));
            }
            return ventas;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo listar las ventas", exception);
        }
    }

    @Override
    public Venta save(Venta venta) {
        if (venta.getIdVenta() == null || venta.getIdVenta() == 0) {
            return insertar(venta);
        } else {
            return actualizar(venta);
        }
    }

    private Venta insertar(Venta venta) {
        String sql = "INSERT INTO venta (fecha, cliente_id, mesa_id, subtotal, total_final) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(venta.getFecha() != null ? venta.getFecha() : LocalDateTime.now()));
            
            if (venta.getCliente() != null && venta.getCliente().getId() != null) {
                statement.setInt(2, venta.getCliente().getId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            
            if (venta.getMesa() != null && venta.getMesa().getId() != null) {
                statement.setInt(3, venta.getMesa().getId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            
            statement.setDouble(4, venta.getSubtotal());
            statement.setDouble(5, venta.getTotalFinal());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    venta.setIdVenta(generatedKeys.getInt(1));
                }
            }
            
            // Guardar relación venta_producto
            if (venta.getProductos() != null && !venta.getProductos().isEmpty()) {
                guardarProductosVenta(venta);
            }
            
            return venta;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo registrar la venta", exception);
        }
    }

    private Venta actualizar(Venta venta) {
        String sql = "UPDATE venta SET fecha = ?, cliente_id = ?, mesa_id = ?, subtotal = ?, total_final = ? WHERE id_venta = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(venta.getFecha() != null ? venta.getFecha() : LocalDateTime.now()));
            
            if (venta.getCliente() != null && venta.getCliente().getId() != null) {
                statement.setInt(2, venta.getCliente().getId());
            } else {
                statement.setNull(2, java.sql.Types.INTEGER);
            }
            
            if (venta.getMesa() != null && venta.getMesa().getId() != null) {
                statement.setInt(3, venta.getMesa().getId());
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            
            statement.setDouble(4, venta.getSubtotal());
            statement.setDouble(5, venta.getTotalFinal());
            statement.setInt(6, venta.getIdVenta());
            
            if (statement.executeUpdate() == 0) {
                throw new NoSuchElementException("No existe una venta con id " + venta.getIdVenta());
            }
            
            // Actualizar productos
            if (venta.getProductos() != null) {
                eliminarProductosVenta(venta.getIdVenta());
                guardarProductosVenta(venta);
            }
            
            return venta;
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo actualizar la venta", exception);
        }
    }

    @Override
    public Venta findById(Integer id) {
        String sql = "SELECT id_venta, fecha, cliente_id, mesa_id, subtotal, total_final FROM venta WHERE id_venta = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                Venta venta = mapearVenta(resultSet);
                cargarProductosVenta(venta, connection);
                return venta;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo buscar la venta", exception);
        }
    }

    private Venta mapearVenta(ResultSet resultSet) throws SQLException {
        Venta venta = new Venta();
        venta.setIdVenta(resultSet.getInt("id_venta"));
        venta.setFecha(resultSet.getTimestamp("fecha") != null 
            ? resultSet.getTimestamp("fecha").toLocalDateTime() 
            : LocalDateTime.now());
        venta.setSubtotal(resultSet.getDouble("subtotal"));
        venta.setTotalFinal(resultSet.getDouble("total_final"));
        
        // Los clientes y mesas se cargan bajo demanda
        return venta;
    }

    private void guardarProductosVenta(Venta venta) throws SQLException {
        String sql = "INSERT INTO venta_producto (venta_id, producto_id) VALUES (?, ?)";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (var producto : venta.getProductos()) {
                statement.setInt(1, venta.getIdVenta());
                statement.setLong(2, producto.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void eliminarProductosVenta(Integer ventaId) throws SQLException {
        String sql = "DELETE FROM venta_producto WHERE venta_id = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ventaId);
            statement.executeUpdate();
        }
    }

    private void cargarProductosVenta(Venta venta, Connection connection) throws SQLException {
        // Este método carga los productos relacionados a la venta
        // Por ahora se deja vacío para evitar consultas N+1
    }

    private void crearTablaSiNoExiste() {
        String sql = "CREATE TABLE IF NOT EXISTS venta (" +
                "id_venta SERIAL PRIMARY KEY, " +
                "fecha TIMESTAMP NOT NULL, " +
                "cliente_id INTEGER, " +
                "mesa_id INTEGER, " +
                "subtotal DECIMAL(10, 2) NOT NULL, " +
                "total_final DECIMAL(10, 2) NOT NULL, " +
                "FOREIGN KEY (cliente_id) REFERENCES cliente(id), " +
                "FOREIGN KEY (mesa_id) REFERENCES mesa(id)" +
                ")";
        
        String junctionTableSql = "CREATE TABLE IF NOT EXISTS venta_producto (" +
                "venta_id INTEGER NOT NULL, " +
                "producto_id BIGINT NOT NULL, " +
                "PRIMARY KEY (venta_id, producto_id), " +
                "FOREIGN KEY (venta_id) REFERENCES venta(id_venta), " +
                "FOREIGN KEY (producto_id) REFERENCES producto(id)" +
                ")";
        
        try (Connection connection = dbConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            statement.execute(junctionTableSql);
        } catch (SQLException exception) {
            throw new IllegalStateException("No se pudo crear las tablas de venta", exception);
        }
    }
}