package com.blackcode.padraocriacional;

import com.blackcode.padraocriacional.factorymethod.api.creator.Creator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PadraocriacionalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PadraocriacionalApplication.class, args);
	}

	@Bean
	CommandLineRunner demo(@Qualifier("creatorA") Creator a,
			@Qualifier("creatorB") Creator b ){
		return args->{
			a.someOperation();
			b.someOperation();
		};
	}
}
