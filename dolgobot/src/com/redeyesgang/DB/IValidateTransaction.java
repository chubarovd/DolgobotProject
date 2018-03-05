package com.redeyesgang.DB;

import java.sql.SQLException;

public interface IValidateTransaction {
    void validate(int transactID) throws SQLException, TransactionException;
}
