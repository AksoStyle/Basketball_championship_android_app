package com.example.basketball_championship.interfaces;

import com.google.firebase.Timestamp;

import java.util.Date;

public interface User {
    String getName();
    String getId();
    String getEmail();
    Date getBirthDate();
    boolean isAdmin();
}
