package com.redeyesgang.DB;

import java.util.Objects;

/**
 * Created by User on 05.03.2018.
 */

public class Transaction extends Object {
    private long fromId;
    private long toId;
    private int amount;
    private long transactID = -1;
    private String description = null;

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

    public Transaction setDescription (String description) {
        this.description = description;
        return this;
    }

    public Transaction (long fromId) {
        super();
        this.fromId = fromId;
        this.toId = -1;
        this.amount = -1;
    }

    public Transaction (long fromId, long toId, int amount) throws TransactionException {
        super();
        if (fromId == toId) {
            throw new TransactionException ("Вы не можете добавть транзакцию самому себе.");
        } else {
            this.fromId = fromId;
            this.toId = toId;
        }
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new TransactionException (
                "Некорректная сумма. Сумма должна быть целым положительным числом.");
        }
    }

    public long getTransactID() {
        return transactID;
    }

    public void setTransactID(long transactID) {
        this.transactID = transactID;
    }
}