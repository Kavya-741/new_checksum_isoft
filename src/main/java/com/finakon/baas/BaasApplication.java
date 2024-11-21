package com.finakon.baas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.finakon.baas.config.SHA256PasswordEncoder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.finakon.baas")
@EnableScheduling
@EnableAsync
@Configuration
@OpenAPIDefinition(info = @Info(
		title = "BaaS API",
		version = "1.0.0"))
public class BaasApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaasApplication.class, args);
	}

}
