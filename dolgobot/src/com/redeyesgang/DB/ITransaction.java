package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;

public interface ITransaction {
    long addTransaction(Transaction trans) throws SQLException;
    Transaction validate(long transactID) throws SQLException, TransactionException;
    Transaction cancel(long transactID) throws SQLException, TransactionException;
    List<Transaction> addTransactionToGroup(long telegramID,String groupName, int amount,String desc) throws SQLException, TransactionException;
    List<Transaction> getTransactions(long telegramID) throws SQLException, TransactionException;
}
