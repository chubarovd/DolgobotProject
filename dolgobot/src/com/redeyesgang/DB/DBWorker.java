package com.redeyesgang.DB;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DBWorker implements IGetInfo,ITransaction,IWorkWithUsers {
    private IGetInfo getter;
    private ITransaction transaction;
    private IWorkWithUsers workWithUsers;

    public DBWorker() throws ClassNotFoundException, IOException, SQLException {
        Class.forName("org.postgresql.Driver");
        Properties props = new Properties();
        props.load(new FileInputStream("query"));
        Properties propsServer = new Properties();
        props.load(new FileInputStream("server"));
        Connection conGet = DriverManager.getConnection(propsServer.getProperty("server"),
                propsServer.getProperty("login"),propsServer.getProperty("password"));
        Connection conTrans = DriverManager.getConnection(propsServer.getProperty("server"),
                propsServer.getProperty("login"),propsServer.getProperty("password"));
        Connection conWWU = DriverManager.getConnection(propsServer.getProperty("server"),
                propsServer.getProperty("login"),propsServer.getProperty("password"));
        getter = new GetInfo(conGet,props);
        transaction = new WTransaction(conTrans,props);
        workWithUsers = new WorkWithUsers(conWWU,props);
    }

    @Override
    public long addTransaction(Transaction trans) throws SQLException {
        return transaction.addTransaction(trans);
    }

    @Override
    public Transaction validate(long transactID) throws SQLException, TransactionException {
        return transaction.validate(transactID);
    }

    @Override
    public Transaction cancel(long transactID) throws SQLException, TransactionException {
        return transaction.cancel(transactID);
    }

    @Override
    public List<Transaction> addTransactionToGroup(long telegramID, String groupName, int amount, String desc) throws SQLException, TransactionException {
        return transaction.addTransactionToGroup(telegramID,groupName,amount,desc);
    }

    @Override
    public List<Transaction> getTransactions(long telegramID) throws SQLException, TransactionException {
        return transaction.getTransactions(telegramID);
    }

    @Override
    public void createUser(long telegramUID, long telegramChatUid, String firstName, String dolgobotLogin, String lastName) throws SQLException, OnCreateException {
        workWithUsers.createUser(telegramUID,telegramChatUid,firstName,dolgobotLogin,lastName);
    }

    @Override
    public void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        workWithUsers.createGroup(telegramUid,groupName);
    }

    @Override
    public void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        workWithUsers.deleteGroup(telegramUid,groupName);
    }
    @Override
    public void addUserToGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        workWithUsers.addUserToGroup(telegramUid,groupName);
    }
    public void addUserToGroupWithCheck(long telegramUidMayBeAdmin,long telegramUid, String groupName) throws SQLException, OnCreateException {
        long adminID =getGroupAdminID(groupName);
        if (telegramUidMayBeAdmin==adminID) addUserToGroup(telegramUid,groupName);
        else throw new OnCreateException("Ошибка доступа");
    }


    @Override
    public void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        workWithUsers.deleteUserFromGroup(telegramUid,groupName);
    }

    public void deleteUserFromGroupWithCheck(long telegramUidMayBeAdmin,long telegramUid, String groupName) throws SQLException, OnCreateException {
        long adminID =getGroupAdminID(groupName);
        if (telegramUidMayBeAdmin==adminID) deleteUserFromGroup(telegramUid,groupName);
        else throw new OnCreateException("Ошибка доступа");
    }
    @Override
    public long getGroupAdminID(String groupName) throws SQLException, OnCreateException {
        return workWithUsers.getGroupAdminID(groupName);
    }

    @Override
    public long getChatIDbyTgUID(long telegramID) throws SQLException, OnCreateException {
        return workWithUsers.getChatIDbyTgUID(telegramID);
    }

    @Override
    public String getLoginByTelegramID(long telegramID) throws SQLException, OnCreateException {
        return workWithUsers.getLoginByTelegramID(telegramID);
    }

    @Override
    public void updateChatID(long telegramUID, long telegramChatID) throws SQLException {
        workWithUsers.updateChatID(telegramUID,telegramChatID);
    }

    @Override
    public long getTelegramIDbyLogin(String login) throws SQLException, OnCreateException {
        return workWithUsers.getTelegramIDbyLogin(login);
    }

    @Override
    public Map<Long, Long> getTotal(long userID) throws SQLException {
        return getter.getTotal(userID);
    }

    @Override
    public List<Long> getGroupInfo(String groupName) throws SQLException, OnCreateException {
        return getter.getGroupInfo(groupName);
    }
}
