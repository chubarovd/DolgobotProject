package com.redeyesgang.tg;

import com.redeyesgang.DB.Transaction;
import com.redeyesgang.DB.TransactionException;
import com.redeyesgang.DB.UserDB;

import java.util.List;
import java.util.Set;

/**
 * Created by User on 06.03.2018.
 */

public class User {
    public enum State {
        SENDS_LOGIN,//
        SENDS_DEST_USER,//
        SENDS_AMOUNT,//
        SENDS_DESCRIPTION,//
        SENDS_GROUP_NAME_CREATION,//
        SENDS_GROUP_NAME_DELETE,//
        SENDS_GROUP_NAME_USERS_ADDITION,//
        SENDS_GROUP_MEMBERS,//
        SENDS_GROUP_NAME_TR,//
        SENDS_AMOUNT_GROUP_TR,//
        SENDS_DESCRIPTION_GROUP_TR//
    }

    private State state;
    private Transaction transaction;
    private String groupName;
    private Set<UserDB> usersList = null;

    public User (State state) {
        this.state = state;
    }
    public User initTransaction (long userId) {
        this.transaction = new Transaction (userId);
        return this;
    }
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
    public String getGroupName () {
        return groupName;
    }
    public User setGroupName (String groupName) {
        this.groupName = groupName;
        return this;
    }
    public Set<UserDB> getUsersList () {
        return usersList;
    }
    public User setUsersList (Set<UserDB> usersList) {
        this.usersList = usersList;
        return this;
    }
}
