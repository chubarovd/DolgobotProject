package com.redeyesgang.DB;

import java.sql.SQLException;

public interface IWorkWithUsers {
    void createUser(int telegramUid, String firstName,String dolgobotLogin) throws SQLException, OnCreateException;
    void createUser(int telegramUid, String firstName,String dolgobotLogin,String lastName) throws SQLException, OnCreateException;
    void createGroup(int telegramUid, String groupName) throws SQLException, OnCreateException;
    void deleteGroup(int telegramUid, String groupName) throws SQLException, OnCreateException;
    void addUserToGroup(int telegramUid,String groupName) throws SQLException, OnCreateException;
    void deleteUserFromGroup(int telegramUid, String groupName) throws SQLException, OnCreateException;
}
