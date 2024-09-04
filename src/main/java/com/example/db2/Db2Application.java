package com.example.db2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Banco de Dados II API", version = "1", description = "API desenvolvida para o trabalho final de banco de dados II."))
public class Db2Application {

	public static void main(String[] args) {
		SpringApplication.run(Db2Application.class, args);
	}

}
