package com.redeyesgang.DB;

/**
 * Created by User on 05.03.2018.
 */

public class Transaction {
    private long fromId;
    private long toId;
    private int amount;
    private int transactID=-1;
    private String description=null;

    public long getFromId () {
        return fromId;
    }
    public long getToId () {
        return toId;
    }
    public int getAmount () {
        return amount;
    }
    public String getDescription () {
        return description;
    }
    public Transaction setFromId (long fromId) {
        this.fromId = fromId;
        return this;
    }
    public Transaction setToId (long toId) {
        this.toId = toId;
        return this;
    }
    public Transaction setAmount (int amount) {
        this.amount = amount;
        return this;
    }

    public Transaction (long fromId, long toId, int amount) throws TransactionException {
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

    public int getTransactID() {
        return transactID;
    }

    public void setTransactID(int transactID) {
        this.transactID = transactID;
    }
}