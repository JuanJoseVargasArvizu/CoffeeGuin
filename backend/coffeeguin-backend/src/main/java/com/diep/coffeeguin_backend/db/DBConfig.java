package com.diep.coffeeguin_backend.db;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class DBConfig {

	private final String url;
	private final String user;
	private final String password;

	public DBConfig() {
		this(
			System.getenv().getOrDefault("DB_URL", "jdbc:h2:mem:testdb"),
			System.getenv().getOrDefault("DB_USER", "sa"),
			System.getenv().getOrDefault("DB_PASS", "")
		);
	}

	public DBConfig(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(user);
		dataSource.setPassword(password);
		return dataSource;
	}
}