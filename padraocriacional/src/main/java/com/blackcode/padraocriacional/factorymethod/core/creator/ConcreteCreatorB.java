package com.blackcode.padraocriacional.factorymethod.core.creator;

import com.blackcode.padraocriacional.factorymethod.api.Product;
import com.blackcode.padraocriacional.factorymethod.api.creator.Creator;
import com.blackcode.padraocriacional.factorymethod.core.product.ConcreteProductB;

/**
 * ConcreteCreatorB
 * @author blackcode
 * @version 1.0
 * @since 2024-06-26
 * ConcreteCreatorB implements the factory method to create an instance of ConcreteProductB.
 * It extends the Creator class and overrides the factoryMethod to return a new ConcreteProductB object.
 * This class is part of the Factory Method design pattern implementation.
 * It encapsulates the instantiation logic for ConcreteProductB, allowing clients to use the Creator interface
 * without needing to know the specifics of the product creation.
 * It promotes loose coupling and adheres to the Open/Closed Principle by allowing new product types
 * to be added without modifying existing code.
 */
public class ConcreteCreatorB extends Creator{

    /**
     * factoryMethod
     * @return Product
     * This method overrides the abstract factoryMethod in the Creator class.
     * It creates and returns an instance of ConcreteProductB, which is a specific implementation of the Product interface.
     * This method encapsulates the instantiation logic for ConcreteProductB, allowing clients to use the Creator interface
     * without needing to know the specifics of the product creation.
     */
    @Override
    protected Product factoryMethod(){
        return new ConcreteProductB();
    }
}
