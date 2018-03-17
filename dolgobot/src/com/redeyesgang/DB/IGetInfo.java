package com.redeyesgang.DB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IGetInfo {
    Map<UserDB,Integer> getTotal(long userID) throws SQLException;
    List<Long> getGroupInfo(String groupName) throws SQLException, OnCreateException;
    List<String> getGroupsNamesForUser(long telegramID) throws SQLException;
    Set<UserDB> getUsersInGroups(long telegramID) throws SQLException;
    List<String> getGroupsForUser(long telegramID) throws SQLException;
}
