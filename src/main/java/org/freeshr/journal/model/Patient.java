package org.freeshr.journal.model;

import org.apache.commons.lang3.StringUtils;

public class Patient {
    private String healthId;
    private String gender;
    private String givenName;
    private String surName;

    public Patient() {
    }

    public String getName() {
        return givenName + " " + surName;
    }

    public String getHealthId() {
        return healthId;
    }

    public String getGender() {
        return gender;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setGender(String gender) {
        this.gender = StringUtils.capitalize(gender);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        return this.healthId.equals(patient.healthId);
    }

    @Override
    public int hashCode() {
        return healthId.hashCode();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "healthId='" + healthId + '\'' +
                ", gender='" + gender + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                '}';
    }
}

