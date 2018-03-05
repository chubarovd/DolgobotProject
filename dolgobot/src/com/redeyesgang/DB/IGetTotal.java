package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.Map;

public interface IGetTotal {
    Map<Integer,Integer> getTotal(int userID) throws SQLException;
}
