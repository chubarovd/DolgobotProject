package com.redeyesgang.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GetInfo implements IGetInfo {
    final private Connection _conn;
    final private Properties _props;

    public GetInfo(Connection con, Properties props) {
        _conn = con;
        _props = props;
    }

    @Override
    public Map<UserDB,Integer> getTotal(long userID) throws SQLException {
        Map<UserDB,Integer> result = new HashMap<>();
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getTotalByID"));
        ps.setLong(1,userID);
        ResultSet rs =  ps.executeQuery();
        while(rs.next()) {
            result.put(getUserInfo(userID), rs.getInt(2));
        }
        rs.close();
        ps.close();
        return result;
    }

    @Override
    public List< Long> getGroupInfo(String groupName) throws SQLException, OnCreateException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new OnCreateException("Такой группы не существует!");
        }
        rs.close();
        ps.close();
        int groupID=0;
        ps = _conn.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        rs = ps.executeQuery();
        if (rs.next()) groupID = rs.getInt(1);
        ps.close();
        rs.close();


        List<Long> res = new ArrayList<>();
        ps = _conn.prepareStatement(QueryBuilderForGroup.getSelectFromGroupQuery("g"+String.valueOf(groupID)));
        rs = ps.executeQuery();
        while(rs.next()){
            res.add(rs.getLong(1));
        }
        return res;
    }

    @Override
    public List<String> getGroupsNamesForUser(long telegramID) throws SQLException {
        List<Integer> groupsID = getGroupsIDsForUser(telegramID);
        List<String> result = new ArrayList<>();
        for(int groupID:groupsID) {
            result.add(getGroupNameByID(groupID));
        }
        return result;
    }

    @Override
    public Set<UserDB> getUsersInGroups(long telegramID) throws SQLException {
        Set<UserDB> result = new HashSet<>();
        List<Integer> groups = getGroupsIDsForUser(telegramID);
        if (groups.isEmpty()) return result;
        for(int group:groups) {
            List<Long> users = getUsersInGroup(group);
            for(long user:users) {
                result.add(getUserInfo(user));
            }
        }
        return result;
    }

    @Override
    public List<String> getGroupsForUser(long telegramID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getGroupsForUser"));
        List<String> groupNames = new ArrayList<>();
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            groupNames.add(rs.getString(1));
        }
        rs.close();
        ps.close();
        return groupNames;

    }

    private String getGroupNameByID (int groupID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getGroupName"));
        ps.setInt(1,groupID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String result = rs.getString(1);
            rs.close();
            ps.close();
            return result;
        }
        else {
            throw new SQLException("Oops!");
        }
    }

    private List<Integer> getGroupsIDsForUser(long telegramID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getUiG"));
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        List<Integer> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getInt(1));
        }
        rs.close();
        ps.close();
        return result;
    }

    private List<Long> getUsersInGroup(int groupID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(
                QueryBuilderForGroup.getSelectFromGroupQuery(getGroupNameByID(groupID)));
        ResultSet rs = ps.executeQuery();
        List<Long> result = new ArrayList<>();
        while(rs.next()) {
            result.add(rs.getLong(1));
        }
        rs.close();
        ps.close();
        return result;
    }

    private UserDB getUserInfo(long telegramID) throws SQLException {
        PreparedStatement ps = _conn.prepareStatement(_props.getProperty("getUserInfo"));
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            long chatID = rs.getLong(1);
            String firstName = rs.getString(2);
            String login = rs.getString(4);
            String secondName = rs.getString(3);
            if (rs.wasNull()) {
                secondName = null;
            }
            rs.close();
            ps.close();
            return new UserDB(telegramID,chatID,firstName,secondName,login);
        }
        else {
            rs.close();
            ps.close();
            throw new SQLException("Oops!");
        }
    }
}
