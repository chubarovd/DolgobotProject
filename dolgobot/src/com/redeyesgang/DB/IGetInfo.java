package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IGetInfo {
    Map<Integer,Integer> getTotal(int userID) throws SQLException;
    List<String> getGroupInfo(String groupName);


}
