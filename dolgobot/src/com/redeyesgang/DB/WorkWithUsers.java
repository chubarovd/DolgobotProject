package com.redeyesgang.DB;

import java.sql.*;
import java.util.Properties;

public class WorkWithUsers implements IWorkWithUsers {
    final private Connection _con;
    final private Properties _props;
    public WorkWithUsers(Connection con, Properties props) {
        _props = props;
        _con = con;
    }

    @Override
    public void createUser(long telegramUid,long telegramChatUid, String firstName, String dolgobotLogin, String lastName) throws SQLException, OnCreateException {
        PreparedStatement ps;
        if (lastName == null) {
            ps = _con.prepareStatement(_props.getProperty("getUser"));
            ps.setString(1,dolgobotLogin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.close();
                ps.close();
                throw new OnCreateException("Пользователь с таким логином сущесвует!");
            }
            rs.close();
            ps.close();
            ps = _con.prepareStatement(_props.getProperty("createUser"));
            ps.setLong(1,telegramChatUid);
            ps.setLong(2,telegramUid);
            ps.setString(3,firstName);
            ps.setString(4,dolgobotLogin);
            ps.executeUpdate();
            ps.close();
            return;
        }
        ps = _con.prepareStatement(_props.getProperty("createUserWithSN"));
        ps.setLong(1,telegramChatUid);
        ps.setLong(2,telegramUid);
        ps.setString(3,firstName);
        ps.setString(4,lastName);
        ps.setString(5,dolgobotLogin);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,"groupName");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            rs.close();
            ps.close();
            throw new OnCreateException("Группа с таким именем существует!");
        }
        rs.close();
        ps.close();

        ps = _con.prepareStatement(_props.getProperty("createGroup"));
        ps.setString(1,groupName);
        ps.setLong(2, telegramUid);
        ps.executeUpdate();
        ps.close();

        Statement stmt = _con.createStatement();
        stmt.execute(QueryBuilderForGroup.getCreateGroupQuery(groupName));
        stmt.close();
        ps = _con.prepareStatement(QueryBuilderForGroup.getInsertToGroupQuery(groupName));
        ps.setLong(1,telegramUid);
        ps.executeUpdate();
        ps.close();

        int groupID=0;
        ps = _con.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        rs = ps.executeQuery();
        if (rs.next()) groupID = rs.getInt(1);
        ps.close();
        rs.close();

        ps = _con.prepareStatement(_props.getProperty("insertUiG"));
        ps.setLong(1,telegramUid);
        ps.setInt(2,groupID);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getAdmin"));
        ps.setString(1,groupName);
        ResultSet rs = ps.executeQuery();
        long adminID;
        if (rs.next()) {
               adminID = rs.getLong(1);
        }
        else {
            rs.close();
            ps.close();
            throw new OnCreateException("Группы с таким именем не сущесвует!");
        }
        rs.close();
        ps.close();
        if (adminID != telegramUid) throw new OnCreateException("Ошибка доступа!");
        Statement stmt = _con.createStatement();
        stmt.execute(QueryBuilderForGroup.getDeleteGroupQuery(groupName));
        stmt.close();
        ps = _con.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        rs = ps.executeQuery();
        int groupID;
        if (rs.next()) {
            groupID = rs.getInt(1);
        }else {
            rs.close();
            ps.close();
            throw new OnCreateException("Группы с таким именем не сущесвует!");
        }
        rs.close();
        ps.close();

        ps = _con.prepareStatement(_props.getProperty("deleteFromUiGAll"));
        ps.setInt(1,groupID);
        ps.executeUpdate();
        ps.close();

        ps = _con.prepareStatement(_props.getProperty("deleteFromGroups"));
        ps.setInt(1,groupID);
        ps.executeUpdate();
        ps.close();

    }

    @Override
    public void addUserToGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        int groupID;
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        ResultSet rs  = ps.executeQuery();
        if(rs.next()) {
            groupID = rs.getInt(1);
        }else {
            throw new OnCreateException("Не сущесвует группы с таким именем!");
        }
        rs.close();
        ps.close();
        try {
            ps = _con.prepareStatement(QueryBuilderForGroup.getInsertToGroupQuery(groupName));
            ps.setLong(1,telegramUid);
            ps.executeUpdate();
            ps.close();
        }catch (SQLException e) {
            throw new OnCreateException("Этот пользователь уже состоит в группе!");
        }



        ps = _con.prepareStatement(_props.getProperty("insertUiG"));
        ps.setLong(1,telegramUid);
        ps.setInt(2,groupID);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException {
        int groupID;
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getGroupID"));
        ps.setString(1,groupName);
        ResultSet rs  = ps.executeQuery();
        if(rs.next()) {
            groupID = rs.getInt(1);
        }else {
            throw new OnCreateException("Не сущесвует группы с таким именем!");
        }
        rs.close();
        ps.close();

        ps = _con.prepareStatement(QueryBuilderForGroup.getDeleteFromGroupQuery(groupName));
        ps.setLong(1,telegramUid);
        ps.executeUpdate();
        ps.close();
        ps = _con.prepareStatement(_props.getProperty("deleteFromUiG"));
        ps.setLong(1,telegramUid);
        ps.setInt(2,groupID);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public long getGroupAdminID(String groupName) throws SQLException, OnCreateException {
        PreparedStatement ps  = _con.prepareStatement(_props.getProperty("getAdmin"));
        ps.setString(1,groupName);
        ResultSet rs = ps.executeQuery();
        long res;

        if (rs.next()) {
            res = rs.getLong(1);
        }else {
            throw new OnCreateException("Не сущесвует группы с таким именем!");
        }
        rs.close();
        ps.close();
        return res;
    }

    @Override
    public long getChatIDbyTgUID(long telegramID) throws SQLException, OnCreateException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getChatIDbyTgUID"));
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        long result;
        if (rs.next()) {
            result = rs.getLong(1);
        }else {
            rs.close();
            ps.close();
            throw new OnCreateException("Такого пользователя нет!");
        }
        rs.close();
        ps.close();
        return result;
    }

    @Override
    public String getLoginByTelegramID(long telegramID) throws SQLException, OnCreateException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getLoginByUID"));
        ps.setLong(1,telegramID);
        ResultSet rs = ps.executeQuery();
        String res;
        if (rs.next()) res = rs.getString(1);
        else {
            rs.close();
            ps.close();
            throw new OnCreateException("Такого пользователя нет!");
        }
        rs.close();
        ps.close();
        return res;

    }

    @Override
    public void updateChatID(long telegramUID, long telegramChatID) throws SQLException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("updateChatID"));
        ps.setLong(1,telegramChatID);
        ps.setLong(2,telegramUID);
        ps.executeUpdate();
        ps.close();
    }

    @Override
    public long getTelegramIDbyLogin(String login) throws SQLException, OnCreateException {
        PreparedStatement ps = _con.prepareStatement(_props.getProperty("getUser"));
        ps.setString(1,login);
        ResultSet rs = ps.executeQuery();
        long res;
        if (rs.next()) res = rs.getLong(1);
        else{
            rs.close();
            ps.close();
            throw new OnCreateException("Такого пользователя нет!");
        }
        rs.close();
        ps.close();
        return res;
    }
}
