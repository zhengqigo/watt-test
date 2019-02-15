package org.fuelteam.watt.test.http;

import java.io.Serializable;

public class Token implements Serializable {

    private static final long serialVersionUID = 6859246547560087128L;

    private String applicationId;

    private String username;

    private String password;

    private String token;

    private String createdAt;

    private String enumed;

    public Token(String applicationId, String username, String password, String token, String createdAt, ReuterKeyEnum reuterKeyEnum) {
        super();
        this.applicationId = applicationId;
        this.username = username;
        this.password = password;
        this.token = token;
        this.createdAt = createdAt;
        this.enumed = reuterKeyEnum.getValue();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUsername() {
        return username;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEnumed() {
        return enumed;
    }

    public void setEnumed(String enumed) {
        this.enumed = enumed;
    }
}
