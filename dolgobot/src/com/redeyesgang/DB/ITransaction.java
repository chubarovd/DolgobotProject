package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;

public interface ITransaction {
    int addTransaction(Transaction trans) throws SQLException;
    void validate(int transactID) throws SQLException, TransactionException;
    void cancel(int transactID) throws SQLException;
    List<Transaction> addTransactionToGroup(int telegramID,String groupName, int amount,String desc) throws SQLException, TransactionException;

}
