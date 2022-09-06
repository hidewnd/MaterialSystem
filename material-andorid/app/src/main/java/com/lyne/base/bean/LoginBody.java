package com.lyne.base.bean;

/**
 * description:
 * time:2022/4/23
 */
public class LoginBody {
    private String username;
    private String password;

    public LoginBody() {
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "LoginBody{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginBody(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
