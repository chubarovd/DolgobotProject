package com.redeyesgang.DB;

public class QueryBuilderForGroup {
    public static String getCreateGroupQuery(String groupName) {
        return  "CREATE TABLE "+groupName+"(member BIGINT REFERENCES users(telegramID));";

    }

    public static String getSelectFromGroupQuery(String groupName) {
        return "SELECT member FROM " + groupName;
    }

    public static String getDeleteGroupQuery(String groupName) {
        return "DROP TABLE " + groupName;
    }

    public static String getInsertToGroupQuery(String groupName) {
        return "INSERT INTO "+groupName+" member VALUES (?)";
    }

    public static String getDeleteFromGroupQuery(String groupName) {
        return "DELETE FROM "+groupName+" WHERE member=?";
    }
}
