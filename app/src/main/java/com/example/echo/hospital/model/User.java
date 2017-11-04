package com.example.echo.hospital.model;

public class User{

    private String account;
    private String password;
    private String name;
    private int identity;
    public static final int ADMIN = 1;
    public static final int AUDITORS = 2;
    public static final int NURSES = 3;
    public static final String PREFS_NAME = "preferences";
    public static final String PREF_ACCOUNT = "Account";
    public static final String PREF_PASSWORD = "Password";
    public static final String PREF_NAME = "Username";
    public static final String PREF_IDENTITY = "Identity";
    public static final String GOOGLE_NAME = "google";
    public static final String GOOGLE_ACCOUNT = "Account";
    public static final String GOOGLE_TYPE = "Type";
    private String googleAccName;
    private String googleAccType;

    public User(){

    }

    public User(String account, String password, int identity){
        setAccount(account);
        setPassword(password);
        setIdentity(identity);
    }

    public String getAccount() {

        return account;
    }

    public void setAccount(String account) {

        this.account = account;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getGoogleAccType() {
        return googleAccType;
    }

    public void setGoogleAccType(String googleAccType) {
        this.googleAccType = googleAccType;
    }

    public String getGoogleAccName() {
        return googleAccName;
    }

    public void setGoogleAccName(String googleAccName) {
        this.googleAccName = googleAccName;
    }
}
