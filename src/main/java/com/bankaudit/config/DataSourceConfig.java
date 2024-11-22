package com.bankaudit.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * {@link DataSourceConfig} class used to configure the {@link DataSource} to
 * project by creating the {@link DriverManagerDataSource} class bean object
 * 
 * @author amit.patel
 *
 */
@Configuration
public class DataSourceConfig {

	@Autowired
    private Environment environment;
	
	@Bean(name = "dataSource")
	public DriverManagerDataSource dataSource() {
	    DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
	    driverManagerDataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driver-class-name"));
	    driverManagerDataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
	    driverManagerDataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
	    driverManagerDataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
	    return driverManagerDataSource;
	}
	
}
