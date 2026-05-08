package com.diep.coffeeguin_backend.db;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DBConnection {

	private final DataSource dataSource;

	public DBConnection(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException exception) {
			throw new IllegalStateException("No se pudo obtener la conexion a la base de datos", exception);
		}
	}
}