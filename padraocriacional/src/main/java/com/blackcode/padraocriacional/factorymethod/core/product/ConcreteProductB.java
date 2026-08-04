package com.blackcode.padraocriacional.factorymethod.core.product;

/**
 * ConcreteProductB
 * @author blackcode
 * @version 1.0
 * @since 2024-06-26
 * @see com.blackcode.padraocriacional.factorymethod.api.Product
 * @category Creational Pattern
 */
public class ConcreteProductB implements com.blackcode.padraocriacional.factorymethod.api.Product{
    public String name() { return "ProductB"; }
    public void execute() { System.out.println("B :: lógica específica"); }
}
