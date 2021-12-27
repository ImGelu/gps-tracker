package edu.utcluj.gpstrack.client;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User implements Serializable {

    private String email;
    private String password;
    private String token;

    public User(String email, String password, String token) {
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}