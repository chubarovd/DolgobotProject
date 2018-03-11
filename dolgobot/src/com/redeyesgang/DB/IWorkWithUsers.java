package com.redeyesgang.DB;

import java.sql.SQLException;

public interface IWorkWithUsers {
    void createUser(int telegramUID,long telegramChatUid, String firstName,String dolgobotLogin,String lastName) throws SQLException, OnCreateException;
    void createGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void deleteGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    void addUserToGroup(long telegramUid,String groupName) throws SQLException, OnCreateException;
    void deleteUserFromGroup(long telegramUid, String groupName) throws SQLException, OnCreateException;
    long getGroupAdminID(String groupName) throws SQLException, OnCreateException;
    long getChatIDbyTgUID(int telegramID) throws SQLException, OnCreateException;
    String getLoginByTelegramID(int telegramID) throws SQLException, OnCreateException;
    void updateChatID(int telegramUID, long telegramChatID) throws SQLException;
    long getTelegramIDbyLogin(String login) throws SQLException, OnCreateException;
}
