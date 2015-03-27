package org.freeshr.journal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentials {
    @JsonProperty("email")
    String email;
    @JsonProperty("password")
    String password;

    public UserCredentials() {
    }

    public UserCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCredentials that = (UserCredentials) o;

        if (!email.equals(that.email)) return false;
        if (!password.equals(that.password)) return false;

        return true;
    }
}
