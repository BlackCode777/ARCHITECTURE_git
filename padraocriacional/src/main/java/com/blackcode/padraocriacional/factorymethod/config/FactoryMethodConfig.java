package com.blackcode.padraocriacional.factorymethod.config;

import com.blackcode.padraocriacional.factorymethod.api.creator.Creator;
import com.blackcode.padraocriacional.factorymethod.core.creator.ConcreteCreatorA;
import com.blackcode.padraocriacional.factorymethod.core.creator.ConcreteCreatorB;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryMethodConfig {

    @Bean
    @Qualifier("creatorA")
    Creator creatorA() {
        return new ConcreteCreatorA();
    }

    @Bean
    @Qualifier("creatorB")
    Creator creatorB() {
        return new ConcreteCreatorB();
    }
}