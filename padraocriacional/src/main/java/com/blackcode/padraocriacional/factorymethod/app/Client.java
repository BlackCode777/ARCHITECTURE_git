package com.blackcode.padraocriacional.factorymethod.app;

import com.blackcode.padraocriacional.factorymethod.api.creator.AccountCreator;
import com.blackcode.padraocriacional.factorymethod.core.creator.CheckingAccountCreator;
import com.blackcode.padraocriacional.factorymethod.core.creator.SavingsAccountCreator;

public class Client{

    public void main( String[] args){

        String kind = args.length > 0 ? args[0] : "checking";
        AccountCreator creator =
                switch (kind) {
                    case "checking" -> new CheckingAccountCreator();
                    case "savings"  -> new SavingsAccountCreator();
                    default         -> throw new IllegalArgumentException("Unknown account type");
                };
                creator.process("BlackCode77", 1000.0);
    }

}
