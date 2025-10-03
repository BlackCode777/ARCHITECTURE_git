package com.blackcode.padraocriacional.factorymethod.core.product;

import com.blackcode.padraocriacional.factorymethod.api.product.Account;

public class CheckingAccount implements Account{

    public CheckingAccount(String owner, double initialDeposit){
        super();
    }

    @Override
    public String type(){
        return "Checking Account";
    }

    @Override
    public void open(){
        System.out.println("Opening a checking account for " + getOwner() + " with initial deposit of $" + getBalance());
    }

    private String getOwner(){
        return null;
    }

    private String getBalance(){
        return null;
    }

}
