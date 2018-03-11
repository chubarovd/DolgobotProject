package com.redeyesgang.tg;

import com.redeyesgang.DB.Transaction;

/**
 * Created by User on 06.03.2018.
 */

public class User {
    public enum State {
        SENDS_LOGIN,
        SENDS_DEST_USER,
        SENDS_AMOUNT,
        SENDS_DESCRIPTION
    }

    //private int userId;
    private State state;
    private Transaction transaction;

    public User () {

    }

    /*public User (int userId) {
        this.userId = userId;
    }*/

    /*public int getUserId () {
        return userId;
    }*/
    public State getState () {
        return state;
    }
    public Transaction getTransaction () {
        return transaction;
    }

    public User setState (State state) {
        this.state = state;
        return this;
    }
}
