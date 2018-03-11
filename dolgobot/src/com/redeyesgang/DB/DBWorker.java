package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBWorker implements IGetInfo,ITransaction,IWorkWithUsers {

    @Override
    public Map<Long, Long> getTotal(long userID) throws SQLException {
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
    public List<Transaction> addTransactionToGroup(int telegramID, String groupName, int amount, String desc) throws SQLException, TransactionException {
        return null;
    }

    @Override
    public void createUser(int telegramUID, long telegramChatUid, String firstName, String dolgobotLogin, String lastName) throws SQLException, OnCreateException {

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

    @Override
    public long getGroupAdminID(String groupName) throws SQLException, OnCreateException {
        return 0;
    }

    @Override
    public long getChatIDbyTgUID(int telegramID) throws SQLException, OnCreateException {
        return 0;
    }

    @Override
    public String getLoginByTelegramID(int telegramID) throws SQLException, OnCreateException {
        return null;
    }

    @Override
    public void updateChatID(int telegramUID, long telegramChatID) throws SQLException {

    }

    @Override
    public long getTelegramIDbyLogin(String login) throws SQLException, OnCreateException {
        return 0;
    }
}
