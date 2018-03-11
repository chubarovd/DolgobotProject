package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;

public interface ITransaction {
    int addTransaction(Transaction trans) throws SQLException;
    Transaction validate(int transactID) throws SQLException, TransactionException;
    Transaction cancel(int transactID) throws SQLException, TransactionException;
    List<Transaction> addTransactionToGroup(long telegramID,String groupName, int amount,String desc) throws SQLException, TransactionException;
    List<Transaction> getTransactions(long telegramID);
}
