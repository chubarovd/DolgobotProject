package com.redeyesgang.DB;

import java.sql.SQLException;

public interface ICreateTransaction {
    int addTransaction(Transaction trans) throws SQLException;
}
