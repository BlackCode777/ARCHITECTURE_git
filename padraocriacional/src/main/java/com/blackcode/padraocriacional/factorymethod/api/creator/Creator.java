package com.blackcode.padraocriacional.factorymethod.api.creator;

import com.blackcode.padraocriacional.factorymethod.api.Product;

public abstract class Creator{

    protected abstract Product factoryMethod();

    public void someOperation(){
        Product product = factoryMethod();
        System.out.println("Creator: The same creator's code has just worked with " + product.name());
        product.execute();
    }

}
