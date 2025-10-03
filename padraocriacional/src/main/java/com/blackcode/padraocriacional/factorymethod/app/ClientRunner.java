package com.blackcode.padraocriacional.factorymethod.app;

import com.blackcode.padraocriacional.factorymethod.api.creator.Creator;
import org.springframework.boot.CommandLineRunner;

public class ClientRunner implements CommandLineRunner{

    private final Creator creatorA;
    private final Creator creatorB;

    public ClientRunner(Creator creatorA, Creator creatorB) {
        this.creatorA = creatorA;
        this.creatorB = creatorB;
    }

    @Override
    public void run(String... args) {
        creatorA.someOperation();
        creatorB.someOperation();
    }
}
