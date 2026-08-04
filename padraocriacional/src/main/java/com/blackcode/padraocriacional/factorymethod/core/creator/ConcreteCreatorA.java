package com.blackcode.padraocriacional.factorymethod.core.creator;

import com.blackcode.padraocriacional.factorymethod.api.Product;
import com.blackcode.padraocriacional.factorymethod.api.creator.Creator;
import com.blackcode.padraocriacional.factorymethod.core.product.ConcreteProductA;

/**
 * Concrete Creator A
 * @author blackcode
 * @version 1.0
 * @since 2024-06-27
 */
public class ConcreteCreatorA extends Creator{
    @Override
    protected Product factoryMethod(){
        return new ConcreteProductA();
    }
}
