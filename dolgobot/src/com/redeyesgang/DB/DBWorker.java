package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBWorker implements IGetInfo,ITransaction,IWorkWithUsers {
    @Override
    public Map<Integer, Integer> getTotal(long userID) throws SQLException {
        return null;
    }

    @Override
    public List<String> getGroupInfo(String groupName) {
        return null;
    }

    @Override
    public int addTransaction(Transaction trans) throws SQLException {
        return 0;
    }

    @Override
    public void validate(int transactID) throws SQLException, TransactionException {

    }

    @Override
    public void cancel(int transactID) throws SQLException {

    }

    @Override
    public void createUser(long telegramUid, String firstName, String dolgobotLogin, String lastName) throws SQLException, OnCreateException {

    }

    @Override
    public void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {

    }

    @Override
    public void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {

    }

    @Override
    public void addUserToGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {

    }

    @Override
    public void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {

    }
}
