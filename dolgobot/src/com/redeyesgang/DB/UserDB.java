package com.redeyesgang.DB;

import java.util.Objects;

public class UserDB {
    long telegramID,telegramChatID;
    String firstName,secondName, login;

    public UserDB(long telegramID, long telegramChatID, String firstName, String secondName, String login) {
        this.telegramID = telegramID;
        this.telegramChatID = telegramChatID;
        this.firstName = firstName;
        this.secondName = secondName;
        this.login = login;
    }

    public long getTelegramID() {
        return telegramID;
    }

    public long getTelegramChatID() {
        return telegramChatID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDB)) return false;
        UserDB userDB = (UserDB) o;
        return getTelegramID() == userDB.getTelegramID() &&
                Objects.equals(getLogin(), userDB.getLogin());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getTelegramID(), getLogin());
    }
}
