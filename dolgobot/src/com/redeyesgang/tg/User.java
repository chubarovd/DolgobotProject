package com.redeyesgang.tg;

import com.redeyesgang.DB.Transaction;

import java.util.List;

/**
 * Created by User on 06.03.2018.
 */

public class User {
    public enum State {
        SENDS_DEST_USER,
        SENDS_AMOUNT
    }

    private int userId;
    private State state;
    private List<Transaction> unverifiedTransactions;

    public User (int userId) {
        this.userId = userId;
    }
}
