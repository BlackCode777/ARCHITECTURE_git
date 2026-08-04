package com.blackcode.padraocriacional.factorymethod.core.creator;

import com.blackcode.padraocriacional.factorymethod.api.creator.AccountCreator;
import com.blackcode.padraocriacional.factorymethod.api.product.Account;
import com.blackcode.padraocriacional.factorymethod.core.product.CheckingAccount;

public class CheckingAccountCreator extends AccountCreator{

    protected Account create(String owner,double initialDeposit){
        return new CheckingAccount(owner, initialDeposit);
    }

}
