package com.redeyesgang.DB;

import java.sql.SQLException;

public interface IWorkWithUsers {
    void createUser(long telegramUID,long telegramChatUid, String firstName,String dolgobotLogin,String lastName) throws SQLException, OnCreateException;
    void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void addUserToGroup(long telegramUid,String groupName) throws SQLException, OnCreateException;
    void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    long getGroupAdminID(String groupName) throws SQLException, OnCreateException;
    long getChatIDbyTgUID(long telegramID) throws SQLException, OnCreateException;
    String getLoginByTelegramID(long telegramID) throws SQLException, OnCreateException;
    void updateChatID(long telegramUID, long telegramChatID) throws SQLException;
    long getTelegramIDbyLogin(String login) throws SQLException, OnCreateException;
}
