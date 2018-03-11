package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IGetInfo {
    Map<Long,Long> getTotal(long userID) throws SQLException;
    List<String> getGroupInfo(String groupName);


}
