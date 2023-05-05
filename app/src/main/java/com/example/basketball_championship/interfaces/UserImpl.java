package com.example.basketball_championship.interfaces;

import java.util.Date;

public class UserImpl implements User{
    private String name;
    private String id;
    private String email;
    private Date birthDate;
    private boolean isAdmin;

    public UserImpl() {}

    public UserImpl(String name, String id, String email, Date birthDate, boolean isAdmin) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.birthDate = birthDate;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public Date getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
