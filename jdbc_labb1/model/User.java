package com.example.jdbc_labb1.model;

/**
 * A representation of an authorized user that can connect to the db.
 **/
public class User {
    private String userName;
    private String psw;

    public User(String userName, String psw) {
        this.userName = userName;
        this.psw = psw;
    }
    public String getUserName() {
        return userName;
    }

    public String getPsw() {
        return psw;
    }
}
