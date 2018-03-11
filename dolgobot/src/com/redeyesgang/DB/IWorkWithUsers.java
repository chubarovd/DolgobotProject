package com.redeyesgang.DB;

import java.sql.SQLException;

public interface IWorkWithUsers {
    void createUser(long telegramUid, String firstName,String dolgobotLogin,String lastName) throws SQLException, OnCreateException;
    void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void addUserToGroup(long telegramUid,String groupName) throws SQLException, OnCreateException;
    void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
}
