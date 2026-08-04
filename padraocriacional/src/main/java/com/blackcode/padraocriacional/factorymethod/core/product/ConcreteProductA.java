package com.blackcode.padraocriacional.factorymethod.core.product;

import com.blackcode.padraocriacional.factorymethod.api.Product;

/**
 * Concrete Products provide various implementations of the Product interface.
 * ConcreteProductA
 * @author blackcode
 * @version 1.0
 * @since 2024-06-20
 *
 * LOCALIZATION Product:
 * src/main/java/com/blackcode/padraocriacional/factorymethod/api/Product.java
 * LOCALIZATION ConcreteProductA:
 * src/main/java/com/blackcode/padraocriacional/factorymethod/core/product/ConcreteProductA.java
 */
public class ConcreteProductA implements Product{
    public String name() { return "ProductA"; }
    public void execute() { System.out.println("A :: lógica específica"); }
}
