package com.diep.coffeeguin_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CoffeeguinBackendApplication {

	public static void main(String[] args) {
		// Load .env if present (ignored if missing)
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		Map<String, Object> defaultProps = new HashMap<>();
		String dbUrl = dotenv.get("DB_URL");
		String dbDriver = dotenv.get("DB_DRIVER");
		String dbUser = dotenv.get("DB_USER");
		String dbPass = dotenv.get("DB_PASS");
		String jpaDdl = dotenv.get("JPA_DDL");
		String jpaShowSql = dotenv.get("JPA_SHOW_SQL");
		String hibernateDialect = dotenv.get("HIBERNATE_DIALECT");

		if (dbUrl != null) defaultProps.put("spring.datasource.url", dbUrl);
		if (dbDriver != null) defaultProps.put("spring.datasource.driverClassName", dbDriver);
		if (dbUser != null) defaultProps.put("spring.datasource.username", dbUser);
		if (dbPass != null) defaultProps.put("spring.datasource.password", dbPass);
		if (jpaDdl != null) defaultProps.put("spring.jpa.hibernate.ddl-auto", jpaDdl);
		if (jpaShowSql != null) defaultProps.put("spring.jpa.show-sql", jpaShowSql);
		if (hibernateDialect != null) defaultProps.put("spring.jpa.properties.hibernate.dialect", hibernateDialect);

		SpringApplication app = new SpringApplication(CoffeeguinBackendApplication.class);
		if (!defaultProps.isEmpty()) {
			app.setDefaultProperties(defaultProps);
		}
		app.run(args);
	}

}
