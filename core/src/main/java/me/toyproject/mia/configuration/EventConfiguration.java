package me.toyproject.mia.configuration;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

public class EventConfiguration {
	@Bean
	@ConfigurationProperties("spring.datasource")
	public HikariDataSource dataSource() throws SQLException {
		Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start();
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}
}
