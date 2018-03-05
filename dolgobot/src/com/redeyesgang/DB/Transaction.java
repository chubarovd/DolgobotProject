package com.redeyesgang.DB;

/**
 * Created by User on 05.03.2018.
 */

public class Transaction {
    private int fromId;
    private int toId;
    private int amount;
    private String description;

    public int getFromId () {
        return fromId;
    }
    public int getToId () {
        return toId;
    }
    public int getAmount () {
        return amount;
    }
    public String getDescription () {
        return description;
    }

    public Transaction (int fromId, int toId, int amount) throws TransactionException {
        if (fromId == toId) {
            throw new TransactionException ("Parameters fromId and toId couldn't be equals.");
        } else {
            this.fromId = fromId;
            this.toId = toId;
        }
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new TransactionException (
                "Invalid amount. It should be positive integer number.");
        }
    }
}