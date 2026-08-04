package com.blackcode.padraocriacional.factorymethod.core.product;

import com.blackcode.padraocriacional.factorymethod.api.product.Account;

public class SavingsAccount implements Account{

    private String owner;
    private double initial;

    public SavingsAccount(String owner, double initialDeposit){
        this.owner = owner;
        this.initial = initialDeposit;
    }

    public String type(){
        return "SavingsAccount";
    }

    public void open(){
        System.out.println("Poupança aberta com " + initial);
    }

    public String getOwner(){
        return owner;
    }

    public double getBalance(){
        return initial;
    }

    public void deposit(double amount){
        if (amount > 0){
            initial += amount;
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    public void withdraw(double amount){
        if (amount > 0 && amount <= initial){
            initial -= amount;
        } else {
            throw new IllegalArgumentException("Invalid withdraw amount");
        }
    }

    public String toString(){
        return "SavingsAccount{" +
                "owner='" + owner + '\'' +
                ", balance=" + initial +
                '}';
    }

}
