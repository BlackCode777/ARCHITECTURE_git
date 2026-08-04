package com.blackcode.padraocriacional.factorymethod.api.creator;

import com.blackcode.padraocriacional.factorymethod.api.product.Account;

public abstract class AccountCreator{

    protected abstract Account create(String owner, double initialDeposit);

    public void process(String owner, double initialDeposit){
        Account account = create(owner, initialDeposit);
        System.out.println("Account created: " + account);
        account.open();
    }

}
