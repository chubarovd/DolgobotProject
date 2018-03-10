package com.redeyesgang.DB;

import java.sql.SQLException;

public interface ITransaction {
    int addTransaction(Transaction trans) throws SQLException;
    void validate(int transactID) throws SQLException, TransactionException;
    void cancel(int transactID) throws SQLException;

}
